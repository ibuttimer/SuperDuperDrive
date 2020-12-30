package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.*;
import com.udacity.jwdnd.course1.cloudstorage.controllers.processors.CredentialsProcessor;
import com.udacity.jwdnd.course1.cloudstorage.controllers.processors.FileProcessor;
import com.udacity.jwdnd.course1.cloudstorage.controllers.processors.NoteProcessor;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.HomeForm;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.*;
import static com.udacity.jwdnd.course1.cloudstorage.controllers.ErrorControllerImpl.getErrorMessage;

/**
 * Home controller
 */
@Controller
public class HomeController extends AbstractController {

    private static final List<String> HOME_ATTRIBUTES = List.of(
        // home
        "home", "logOut", "fileSubmit", "close", "save_changes", "files", "notes", "credentials",
        // files
        "upload_new_file", "view", "download", "delete", "edit",
        // notes
        "add_new_note", "note", "title", "description", "notetitleMaxLen", "notedescriptionMaxLen",
        // credentials
        "add_new_credentials", "url", "username", "password", "credential", "urlMaxLen", "credentialUsernameMaxLen",
        "credentialPasswordMaxLen"
    );

    private static final List<String> RESULT_ATTRIBUTES = List.of(
        "result", "success", "error", "warning",
        "clickToContinue0", "clickToContinue1", "clickToContinue2"

    );

    // resource keys of item texts
    private static final Map<Tabs, String> TABS_STRING_MAP = Map.of(
            Tabs.file_tab, "resultFile",
            Tabs.note_tab, "resultNote",
            Tabs.credentials_tab, "resultCredentials"
    );

    // generate the result messages required for action outcomes for the result html template
    private final Map<Integer, String> RESULT_MESSAGES;
    {
        ResourceBundle bundle = ResourceStore.getBundle();
        Map<Integer, String> map = new HashMap<>();
        for (Tabs tab : Tabs.values()) {
            String item = TABS_STRING_MAP.get(tab);
            if (item != null) {
                for (Action action : Action.values()) {
                    switch (action) {
                        case create:
                        case update:
                        case delete:
                            for (Result result : Result.values()) {
                                switch (result) {
                                    case Success:
                                    case NotSaved:
                                    case Error:
                                        Integer key = messageKey(tab, action, result);
                                        String template = bundle.getString(
                                                templateResource(action, result));
                                        String itemText = bundle.getString(item);

                                        String caseResource = templateResourceCase(action, result);
                                        if (bundle.containsKey(caseResource)) {
                                            switch (bundle.getString(caseResource)) {
                                                case "lower":
                                                    itemText = itemText.toLowerCase(Locale.ROOT);
                                                    break;
                                                case "upper":
                                                    itemText = itemText.toUpperCase(Locale.ROOT);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }

                                        map.put(key, String.format(template, itemText));
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        RESULT_MESSAGES = map;
    }

    private final UserService userService;
    private final FileService fileService;
    private final NoteService noteService;
    private final CredentialsService credentialsService;

    private final FileProcessor fileProcessor;
    private final NoteProcessor noteProcessor;
    private final CredentialsProcessor credentialsProcessor;

    /**
     * Constructor
     */
    public HomeController(UserService userService, FileService fileService, NoteService noteService,
                          CredentialsService credentialsService,
                          FileProcessor fileProcessor, NoteProcessor noteProcessor, CredentialsProcessor credentialsProcessor) {
        this.userService = userService;
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialsService = credentialsService;

        this.fileProcessor = fileProcessor;
        this.noteProcessor = noteProcessor;
        this.credentialsProcessor = credentialsProcessor;
    }

    /**
     * Handle read & delete operations
     * @param authentication - current authentication token
     * @param tab - tab from request, one of {@link Tabs}
     * @param action - request action, one of {@link Action}
     * @param id - resource id
     * @param homeForm - form data
     * @param model - model
     * @return  name of template
     */
    @GetMapping(HOME_URL)
    public String get(Authentication authentication,
                      @RequestParam(required = false) String tab,
                      @RequestParam(required = false) String action,
                      @RequestParam(required = false) String id,
                      @ModelAttribute("homeForm") HomeForm homeForm,
                      Model model) {
        Tabs tabEnum = Tabs.from(tab);
        Action actionEnum = Action.from(action);
        String template;
        ActionResult result = ActionResult.ofSuccess(0, userService.getUserId(authentication));

        if (tabEnum == Tabs.no_tab) {
            // request for home
            result.setTab(Tabs.file_tab);
            template = homeView(result, model);
        } else if (actionEnum == Action.select) {
            // select requested tab
            result.setTab(tabEnum);
            template = homeView(result, model);
        } else {
            // request to process action from tab
            result = process(authentication, tabEnum, actionEnum, id, homeForm);
            template = resultView(result, model);
        }
        return template;
    }

    /**
     * Get the clear text password for the credentials with the specified id
     * @param authentication - current authentication token
     * @param id - credentials id
     * @return
     */
    @GetMapping(GET_CREDENTIALS_PASSWORD_URL)
    public ResponseEntity<?> getPassword(Authentication authentication,
                                      @RequestParam String id) {

        ErrorCode errorCode = ErrorCode.notfound;
        ResponseEntity<?> response = null;

        if (credentialsService.verifyAuthentication(authentication, id).getLeft()) {
            Credentials credentials = credentialsService.getCredentialsPlainText(Integer.parseInt(id));
            if (credentials != null) {
                response = new ResponseEntity<>(credentials.getPassword(), HttpStatus.OK);
                errorCode = ErrorCode.none;
            }
        } else {
            errorCode = ErrorCode.unauthorised;
        }

        if (errorCode != ErrorCode.none) {
            response = errorResponse(errorCode);
        }
        return response;
    }

    /**
     * Update the model and select home template
     * @param result - action result
     * @param model - model
     * @return  name of template
     */
    private String homeView(ActionResult result, Model model) {
        addModelAttributes(model, HOME_ATTRIBUTES);

        result.getUserId().ifPresent(uId -> {
            model.addAttribute("allFiles", fileService.getAllForUser(uId));
            model.addAttribute("allNotes", noteService.getAllForUser(uId));
            model.addAttribute("allCredentials", credentialsService.getAllForUser(uId));
        });

        model.addAttribute(result); // add 'actionResult' to model

        return "home";
    }

    /**
     * Update the model and select result template
     * @param result - action result
     * @param model - model
     * @return  name of template
     */
    private String resultView(ActionResult result, Model model) {
        addModelAttributes(model, RESULT_ATTRIBUTES);

        model.addAttribute(result); // add 'actionResult' to model

        return "result";
    }

    /**
     * Handle create & update operations
     * @param authentication - current authentication token
     * @param tab - tab from request, one of {@link Tabs}
     * @param homeForm - form data
     * @param model - model
     * @return  name of template
     */
    @PostMapping(HOME_URL)
    public String post(Authentication authentication,
                       @RequestParam String tab,
                       @ModelAttribute("homeForm") HomeForm homeForm,
                       Model model) {
        Action action = Action.from(homeForm.getAction());
        ActionResult result = process(
                authentication, Tabs.from(tab), action, null, homeForm);

        return resultView(result, model);
    }

    /**
     * Upload a file to the database
     * @param authentication - current authentication token
     * @param fileUpload - file to upload
     * @param model - model
     * @return
     */
    @PostMapping(UPLOAD_URL)
    public String uploadFile(Authentication authentication,
                      @RequestParam("fileUpload") MultipartFile fileUpload,
                      Model model) {

        // no update file functionality
        ActionResult result = null;
        String extMsgResource = null;

        if (StringUtils.isEmptyOrWhitespace(fileUpload.getOriginalFilename())) {
            result = ActionResult.ofNotSaved(0, userService.getUserId(authentication));
            extMsgResource = "noFileSelected";
        } else if (!fileService.isFilenameAvailable(fileUpload.getOriginalFilename())) {
            result = ActionResult.ofError(0, userService.getUserId(authentication));
            extMsgResource = "fileAlreadyExists";
        }

        if (result != null) {
            result.setMessage(ResourceStore.getBundle().getString(extMsgResource));
            setResultMessage(result, Tabs.file_tab, Action.create);
        }
        else {
            HomeForm homeForm = new HomeForm();
            homeForm.setMultipartFile(fileUpload);

            result = process(
                    authentication, Tabs.file_tab, Action.create, null, homeForm);
        }
        return resultView(result, model);
    }

    /**
     * Download a file from the database
     * @param authentication - current authentication token
     * @param id - id of file to download
     * @param target - download target
     * @return
     */
    @GetMapping(DOWNLOAD_URL)
    public ResponseEntity<?> downloadFromDB(Authentication authentication,
                                         @RequestParam String id,
                                         @RequestParam(required = false) String target) {

        ResponseEntity<?> response = null;
        ErrorCode errorCode = ErrorCode.none;
        Pair<Boolean, File> result = fileService.verifyAuthentication(authentication, id);

        if (result.getLeft()) {
            File file = result.getRight();
            if (file != null) {
                response = ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(file.getContenttype()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                DownloadTarget.from(target) + "; filename=\"" + file.getFilename() + "\"")
                        .body(file.getFiledata());
            } else {
                errorCode = ErrorCode.notfound;
            }
        } else {
            errorCode = ErrorCode.unauthorised;
        }
        if (errorCode != ErrorCode.none) {
            response = errorResponse(errorCode);
        }

        return response;
    }

    /**
     * Redirect to error
     * @param errorCode - error code
     * @return
     */
    public ResponseEntity<?> errorResponse(ErrorCode errorCode) {
        ResponseEntity<?> response = null;
        if (errorCode != ErrorCode.none) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(
                    errorRedirect(errorCode));
            response = new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        }
        return response;
    }

    /**
     * Process a request
     * @param authentication - current authentication token
     * @param tab - tab request originated from
     * @param action - action to perform
     * @param id - resource id
     * @param homeForm - form data
     * @return
     */
    private ActionResult process(Authentication authentication,
                                 Tabs tab,
                                 Action action,
                                 String id,
                                 HomeForm homeForm) {

        AtomicReference<ActionResult> atomicResult = new AtomicReference<>();
        OptionalInt userId = userService.getUserId(authentication);

        userId.ifPresent( uId -> {
            ActionResult processResult = null;

            homeForm.setUserid(uId);

            switch (tab) {
                case file_tab:
                    processResult = fileProcessor.process(uId, action,
                            id == null ? String.valueOf(homeForm.getFileid()) : id, homeForm);
                    break;
                case note_tab:
                    processResult = noteProcessor.process(uId, action,
                            id == null ? String.valueOf(homeForm.getNoteid()) : id, homeForm);
                    break;
                case credentials_tab:
                    processResult = credentialsProcessor.process(uId, action,
                            id == null ? String.valueOf(homeForm.getCredentialid()) : id, homeForm);
                    break;
                default:
                    processResult = ActionResult.ofError(0, userId, ErrorCode.notfound);
                    break;
            }
            if (processResult != null) {
                // set the result message
                atomicResult.set(
                        setResultMessage(processResult, tab, action));
            }
        });
        return atomicResult.get();
    }

    private ActionResult setResultMessage(ActionResult processResult, Tabs tab, Action action) {

        String item = TABS_STRING_MAP.get(tab);
        if (item != null && processResult != null) {
            // set the result message
            String baseMessage;
            if (processResult.isError() && processResult.getErrorCode() != ErrorCode.none) {
                // use message corresponding to error code
                baseMessage = getErrorMessage(processResult.getErrorCode());
            } else {
                // use standard message
                baseMessage = String.format(
                        RESULT_MESSAGES.get(messageKey(tab, action, processResult.getResult())),
                        ResourceStore.getBundle().getString(item)
                );
                String extendedMessage = processResult.getMessage();
                if (!StringUtils.isEmpty(extendedMessage)) {
                    baseMessage += " " + extendedMessage;
                }
            }
            processResult.setMessage(baseMessage);

            processResult.setTab(tab);
        }
        return processResult;
    }

    public enum Tabs {
        no_tab, file_tab, note_tab, credentials_tab;

        public static Tabs from(String tabStr) {
            Tabs tab = Tabs.no_tab;
            if (!StringUtils.isEmptyOrWhitespace(tabStr)) {
                tab = Tabs.valueOf(tabStr);
            }
            return tab;
        }
    }

    /**
     * Generate a key for the message map
     * @param tab - tab which initiated
     * @param action - action to perform
     * @param result - result
     * @return
     */
    private static Integer messageKey(Tabs tab, Action action, Result result) {
        return (tab.ordinal() * 100) + (action.ordinal() * 10) + result.ordinal();
    }

    /**
     * Get the name of the resource to use as a template
     * @param action - action to perform
     * @param result - result
     * @return
     */
    private static String templateResource(Action action, Result result) {
        // keys for templates in resource file are of form; <action>Result<result>
        // e.g. "createResultSuccess"
        return action.toString() + "Result" + result.toString();
    }

    /**
     * Get the name of the resource to use as character case control
     * @param action - action to perform
     * @param result - result
     * @return
     */
    private static String templateResourceCase(Action action, Result result) {
        // keys for item case in resource file are of form; <action>Result<result>Case
        // e.g. "createResultSuccessCase"
        return templateResource(action, result) + "Case";
    }
}