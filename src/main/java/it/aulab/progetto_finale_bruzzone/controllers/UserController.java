package it.aulab.progetto_finale_bruzzone.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_bruzzone.services.ArticleService;
import it.aulab.progetto_finale_bruzzone.services.CategoryService;
import it.aulab.progetto_finale_bruzzone.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import it.aulab.progetto_finale_bruzzone.models.Article;
import it.aulab.progetto_finale_bruzzone.models.User;
import it.aulab.progetto_finale_bruzzone.repositories.ArticleRepository;
import it.aulab.progetto_finale_bruzzone.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_bruzzone.dtos.ArticleDto;
import it.aulab.progetto_finale_bruzzone.dtos.UserDto;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ModelMapper modelMapper;

    // Rotta di home 
    @GetMapping("/")
    public String home(Model viewModel) {
        List<ArticleDto> articles = new ArrayList<>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDto.class)); // Usato modelMapper
        }

        // Ordino e invio al template gli articoli ordinati in modo decrescente
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());

        // Prima pagina: un articolo d'apertura, tre "spalle" e la griglia delle ultime notizie
        viewModel.addAttribute("title", "Prima pagina");
        viewModel.addAttribute("articles", articles);
        viewModel.addAttribute("featured", articles.isEmpty() ? null : articles.get(0));
        viewModel.addAttribute("secondary", articles.stream().skip(1).limit(3).collect(Collectors.toList()));
        viewModel.addAttribute("latest", articles.stream().skip(4).limit(6).collect(Collectors.toList()));
        viewModel.addAttribute("more", articles.stream().skip(10).limit(5).collect(Collectors.toList()));
        return "home";
    }

    // Rotta per la registrazione
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    // Rotta per la login
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // Rotta per il salvataggio della registrazione
    
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result,
                               Model model, RedirectAttributes redirectAttributes,
                               HttpServletRequest request, HttpServletResponse response) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already a user registered with the email provided");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        userService.saveUser(userDto, redirectAttributes, request, response);
        redirectAttributes.addFlashAttribute("successMessage", "Registrazione completata: benvenuto nella redazione!");
        return "redirect:/";
    }

    // Rotta per la ricerca degli articoli di un utente
    @GetMapping("/search/{id}")
    public String userArticleSearch(@PathVariable("id") Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "Articoli di " + user.getUsername());

        List<ArticleDto> articles = articleService.searchByAuthor(user);

        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);
        return "article/articles";
    }

    // Rotta per la dashboard dell'admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Richieste di collaborazione");
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "admin/dashboard";
    }


 // Rotta per la dashboard del revisor
    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Articoli da revisionare");
        viewModel.addAttribute("articles", articleRepository.findByIsAcceptedNull());
        
        return "revisor/dashboard";
    }

    // Rotta per la dashboard del writer 
    @GetMapping("/writer/dashboard")
    public String writerDashboard(Model viewModel , Principal principal) {
        viewModel.addAttribute("title", "I miei articoli");
        
        List<ArticleDto> userArticles = articleService.readAll()
                                                      .stream()
                                                      .filter(article -> article.getUser().getEmail().equals(principal.getName()))
                                                      .toList();
        
        viewModel.addAttribute("articles", userArticles);
         
        return "writer/dashboard";
    }

}
