package com.blogboard.server.web;

import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import com.blogboard.server.data.entity.Board;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

@Controller
public class ServicesController {

    private AccountService accountService;
    private BoardService boardService;

    //TODO: in future will have to ensure repositories are only accessed by one call at a time
    //will need resource locking
    private AccountRepository accountRepo;
    private SessionRepository sessionRepo;
    private BoardRepository boardRepo;

    @Autowired (required = true)
    public void setSessionRepository(BoardRepository boardRepository) {
        this.boardRepo = boardRepository;
    }

    @Autowired (required = true)
    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }


    @Autowired (required = true)
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired (required = true)
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired (required = true)
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepo = sessionRepository;
    }

    @RequestMapping(value ="/account", method=RequestMethod.POST)
    public @ResponseBody
    AccountServiceResponse createAccount(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            @RequestParam(value="email", required=false, defaultValue="") String email,
            HttpServletResponse createAccountResponse){

        return accountService.createAccount(accountRepo, username, password, email, createAccountResponse);
    }

    @RequestMapping(value ="/account", method=RequestMethod.GET)
    public @ResponseBody
    AccountServiceResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            HttpServletResponse loginResponse){

        return accountService.login(accountRepo, sessionRepo, username, password, loginResponse);
    }

    @RequestMapping(value ="/session", method=RequestMethod.POST) //better http call option?
    public @ResponseBody
    AccountServiceResponse logout(
            @CookieValue(value = "sessionUsername", defaultValue = "undefined", required = false) String usernameCookie,
            @CookieValue(value = "sessionID", defaultValue = "undefined", required = false) String sessionIDCookie,
            HttpServletResponse loginResponse){

        return accountService.logout(sessionRepo, usernameCookie, sessionIDCookie, loginResponse);
    }

    @RequestMapping(value = "/session", method=RequestMethod.GET)
    public @ResponseBody
    AccountServiceResponse validateSession(
            @CookieValue(value = "sessionID", defaultValue = "undefined", required = false) String validationCookie,
            HttpServletResponse validationResponse) {
        return accountService.validateUserSession(sessionRepo, validationResponse, validationCookie);
    }

    @RequestMapping(value ="/home", method=RequestMethod.GET)
    public ModelAndView getHomePage(
            @CookieValue(value = "sessionUsername", required = true) String sessionUsername) {
        ModelAndView mav = new ModelAndView();

        ArrayList<Board> userBoards = boardService.getListBoards(boardRepo, sessionUsername);
        mav.addObject("userBoards", userBoards);
        mav.setViewName("home");
        return mav;
    }

    @RequestMapping(value = "/board", method=RequestMethod.POST)
    public @ResponseBody
    BoardServiceResponse createBoard(
            @RequestParam(value="boardName", required=true) String boardName,
            @CookieValue(value = "sessionUsername", defaultValue = "undefined", required = false) String usernameCookie,
            HttpServletResponse createAccountServletResponse) {
        return boardService.createBoard(boardRepo, boardName, usernameCookie, createAccountServletResponse);
    }

    @RequestMapping(value ="/board={boardName}", method=RequestMethod.GET)
    public ModelAndView getBoardPage(@PathVariable String boardName,
        @CookieValue(value = "sessionUsername", defaultValue = "undefined", required = false) String sessionUsername,
        @CookieValue(value = "sessionID", defaultValue = "undefined", required = false) String sessionId) {
        ModelAndView mav = new ModelAndView();

        //add info from repo
        //1. check sessionId for validity (do later)
        BoardServiceResponse getBoardResponse = boardService.getBoard(boardRepo, boardName, sessionUsername);

        //create object to add to pebble board template
        mav.addObject("boardName", getBoardResponse.getBoard().getName());
        mav.addObject("boardOwner", getBoardResponse.getBoard().getOwnerUsername());
        mav.addObject("dateCreated", getBoardResponse.getBoard().getDateCreated());
        mav.addObject("boardMembers", getBoardResponse.getBoard().getMembers());
        mav.setViewName("board");
        return mav;
    }

}