package it.aulab.progetto_finale_bruzzone.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_bruzzone.dtos.ArticleDto;
import it.aulab.progetto_finale_bruzzone.dtos.CategoryDto;
import it.aulab.progetto_finale_bruzzone.models.Article;
import it.aulab.progetto_finale_bruzzone.models.Category;
import it.aulab.progetto_finale_bruzzone.repositories.ArticleRepository;
import it.aulab.progetto_finale_bruzzone.services.ArticleService;
import it.aulab.progetto_finale_bruzzone.services.CrudService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto, Category, Long> categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Rotta index articoli 
    @GetMapping
    public String articleIndex(Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli");
        List<ArticleDto> articles = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDto.class));
        }

        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);
        return "article/articles";
    }

    // Rotta per la creazione di un articolo 
    @GetMapping("/create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea un articolo");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }
    // Rotta per lo store di un articolo 
    @PostMapping
    public String articleStore(@Valid @ModelAttribute("article") Article article,
    BindingResult result,
    RedirectAttributes redirectAttributes,
    Principal principal,
    MultipartFile file,
    Model viewModel) {
        // Controllo degli errori di validazione 
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un articolo");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }
        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo creato con successo");
        return "redirect:/";
    }

    // Rotta di dettaglio di un articolo 
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        ArticleDto article = articleService.read(id);
        viewModel.addAttribute("title", article.getTitle());
        viewModel.addAttribute("article", article);
        return "article/detail";
    }

    // Rotta per la modifica di un articolo 
    @GetMapping("/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Modifica articolo");
        viewModel.addAttribute("article", articleService.read(id));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/edit";
    }

    // Rotta di memorizzazione modifica articolo 
    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal,
                                MultipartFile file,
                                Model viewModel) {

// Controllo degli errori di validazione 
if (result.hasErrors()) {
    viewModel.addAttribute("title", "Modifica articolo");
    article.setImage(articleService.read(id).getImage());
    viewModel.addAttribute("article", article);
    viewModel.addAttribute("categories", categoryService.readAll());
    return "article/edit";
}

articleService.update(id, article, file);
redirectAttributes.addFlashAttribute("successMessage", "Articolo modificato con successo");
return "redirect:/articles";
}

// Rotta per la cancellazione di un  articolo 
@GetMapping("/delete/{id}")
public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    articleService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Articolo cancellato con successo");
    return "redirect:/writer/dashboard";
}



    // Rotta di dettaglio di un articolo per il revisore 
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    // Rotta dedicata all'azione del revisore 
    @PostMapping("/accept")
    public String articlesSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleid, RedirectAttributes redirectAttributes) {
        if (action.equals("accept")) {
            articleService.setIsAccepted(true , articleid);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo accettato");
        } else if (action.equals("reject")) {
            articleService.setIsAccepted(false , articleid);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo rifiutato");
        }else{
            redirectAttributes.addFlashAttribute("resultMessage", "Operazione non riuscita");
        }

        return "redirect:/revisor/dashboard";
        
    }

    // Rotta di ricerca di un articolo 
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "Risultati della ricerca");

        List<ArticleDto> articles = articleService.search(keyword);

        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);

        
        return "article/articles";
    }
    

}
