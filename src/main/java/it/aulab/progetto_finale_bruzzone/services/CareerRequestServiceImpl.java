package it.aulab.progetto_finale_bruzzone.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.aulab.progetto_finale_bruzzone.models.CareerRequest;
import it.aulab.progetto_finale_bruzzone.models.Role;
import it.aulab.progetto_finale_bruzzone.models.User;
import it.aulab.progetto_finale_bruzzone.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_bruzzone.repositories.RoleRepository;
import it.aulab.progetto_finale_bruzzone.repositories.UserRepository;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();

        if (!allUserIds.contains(user.getId())) {
            return false;
        }

        List<Long> requests = careerRequestRepository.findByUserId(user.getId());

        return requests.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    public void save (CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        // Invio Mail di richiesta del ruolo a ADMIN 
        emailService.sendSimpleEmail("adminAulabpost@admin.com" , "Richiesta di ruolo: " + careerRequest.getRole().getName(), "C'è una nova richiesta di collaborazione: " + user.getUsername());
    }

   

    @Override
    public void careerAccept(Long requestId) {
        // Recupero la richiesta 
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        // Dalla richiesta estraggo l'utente richiedente ed il ruolo richiesto 
        User user = request.getUser();
        Role role = request.getRole();

        // Recupero tutti i ruoli che l'utente già possiede ed aggiungo quello nuovo 
        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        // Aggiorno il ruolo dell'utente
        user.setRoles(rolesUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail( user.getEmail() , "Richiesta di ruolo accettata", "Il tuo ruolo " + role.getName() + " e' stato accettato");

    }

    @Override
    public CareerRequest find(Long id) {
       return careerRequestRepository.findById(id).get();
    }
}


