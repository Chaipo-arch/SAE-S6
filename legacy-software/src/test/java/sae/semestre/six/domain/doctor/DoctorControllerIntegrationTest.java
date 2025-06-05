package sae.semestre.six.domain.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // S'assure que chaque test s'exécute dans une transaction qui est annulée après l'exécution
public class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllDoctors() throws Exception {
        // Préparer les données de test
        Doctor doctor1 = createDoctor("DR001", "Dr. Dupont","Dupond" ,"Cardiologie", "Cardiologie");
        Doctor doctor2 = createDoctor("DR002", "Dr. Martin","Martines", "Neurologie", "Neurologie");

        doctorDao.save(doctor1);
        doctorDao.save(doctor2);

        // Exécuter et vérifier
        mockMvc.perform(get("/doctor"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].doctorNumber", hasItems("DR001", "DR002")))
                .andExpect(jsonPath("$[*].firstName", hasItems("Dr. Dupont", "Dr. Martin")));
    }

    @Test
    public void testGetDoctorByNumber_Found() throws Exception {
        // Préparer les données de test
        Doctor doctor = createDoctor("DR003", "Dr. Blanc","Fabrice", "Dermatologie", "Dermatologie");
        doctorDao.save(doctor);

        // Exécuter et vérifier
        mockMvc.perform(get("/doctor/number/{doctorNumber}", "DR003"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorNumber", is("DR003")))
                .andExpect(jsonPath("$.firstName", is("Dr. Blanc")))
                .andExpect(jsonPath("$.specialization", is("Dermatologie")));
    }

    @Test
    public void testGetDoctorByNumber_NotFound() throws Exception {
        // Exécuter et vérifier
        mockMvc.perform(get("/doctor/number/{doctorNumber}", "DR999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDoctorsBySpecialization() throws Exception {
        // Préparer les données de test
        Doctor doctor1 = createDoctor("DR004", "Dr. Dubois","Jean" ,"Pédiatrie", "Pédiatrie");
        Doctor doctor2 = createDoctor("DR005", "Dr. Petit", "Jean","Pédiatrie", "Pédiatrie");

        doctorDao.save(doctor1);
        doctorDao.save(doctor2);

        // Exécuter et vérifier
        mockMvc.perform(get("/doctor/specialization/{specialization}", "Pédiatrie"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].doctorNumber", hasItems("DR004", "DR005")))
                .andExpect(jsonPath("$[*].specialization", everyItem(is("Pédiatrie"))));
    }

    @Test
    public void testGetDoctorsByDepartment() throws Exception {
        // Préparer les données de test
        Doctor doctor1 = createDoctor("DR006", "Dr. Leroy", "Cli","Chirurgie", "Bloc opératoire");
        Doctor doctor2 = createDoctor("DR007", "Dr. Moreau", "Jean","Anesthésie", "Bloc opératoire");

        doctorDao.save(doctor1);
        doctorDao.save(doctor2);

        // Exécuter et vérifier
        mockMvc.perform(get("/doctor/department/{department}", "Bloc opératoire"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].doctorNumber", hasItems("DR006", "DR007")))
                .andExpect(jsonPath("$[*].department", everyItem(is("Bloc opératoire"))));
    }

    @Test
    public void testSaveDoctor() throws Exception {
        // Créer un objet Doctor pour la sauvegarde
        Doctor doctor = createDoctor("DR008", "Dr. Simon", "Douziech","Ophtalmologie", "Ophtalmologie");

        // Exécuter et vérifier
        mockMvc.perform(post("/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctorNumber", is("DR008")))
                .andExpect(jsonPath("$.firstName", is("Dr. Simon")));

        // Vérifier que le docteur est bien dans la base de données
        mockMvc.perform(get("/doctor/number/{doctorNumber}", "DR008"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorNumber", is("DR008")));
    }

    @Test
    public void testDeleteDoctor() throws Exception {
        // Préparer les données de test
        Doctor doctor = createDoctor("DR009", "Dr. Fournier","Valentine", "Psychiatrie", "Psychiatrie");
        doctorDao.save(doctor);

        Long doctorId = getDoctorIdByNumber("DR009");

        // Exécuter la suppression
        mockMvc.perform(delete("/doctor/{id}", doctorId))
                .andExpect(status().isOk());

        // Vérifier que le docteur a bien été supprimé
        mockMvc.perform(get("/doctor/number/{doctorNumber}", "DR009"))
                .andExpect(status().isNotFound());
    }

    // Méthode utilitaire pour créer un objet Doctor
    private Doctor createDoctor(String doctorNumber, String name,String lastName, String specialization, String department) {
        Doctor doctor = new Doctor();
        doctor.setDoctorNumber(doctorNumber);
        doctor.setFirstName(name);
        doctor.setLastName(lastName);

        doctor.setSpecialization(specialization);
        doctor.setDepartment(department);
        return doctor;
    }

    // Méthode utilitaire pour récupérer l'ID d'un docteur par son numéro
    private Long getDoctorIdByNumber(String doctorNumber) {
        return doctorDao.findByDoctorNumber(doctorNumber)
                .map(Doctor::getId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with number: " + doctorNumber));
    }
}