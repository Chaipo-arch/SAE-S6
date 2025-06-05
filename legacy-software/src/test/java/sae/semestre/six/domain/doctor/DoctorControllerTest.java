package sae.semestre.six.domain.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {


    @Mock
    private DoctorDao doctorDao;

    private DoctorService doctorService;

    private DoctorController doctorController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        doctorService = new DoctorService(doctorDao);
        doctorController = new DoctorController(doctorDao,doctorService);
    }

    @Test
    public void testGetAllDoctors() {
        // Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDoctorNumber("DR001");
        doctor1.setFirstName("Dupont");
        doctor1.setLastName("Dupond");
        doctor1.setSpecialization("Cardiologie");
        doctor1.setDepartment("Cardiologie");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDoctorNumber("DR002");
        doctor2.setLastName("Dr. Martin");
        doctor2.setFirstName("Dr");
        doctor2.setSpecialization("Neurologie");
        doctor2.setDepartment("Neurologie");

        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);

        when(doctorDao.findAll()).thenReturn(doctors);

        // Act
        List<DoctorDTO> result = doctorController.getAllDoctors();

        // Assert
        assertEquals(2, result.size());
        assertEquals("DR001", result.get(0).getDoctorNumber());
        assertEquals("DR002", result.get(1).getDoctorNumber());
        verify(doctorDao, times(1)).findAll();
    }

    @Test
    public void testGetDoctorByNumber_Found() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setDoctorNumber("DR001");
        doctor.setFirstName("Dupont");
        doctor.setLastName("Dupond");

        doctor.setSpecialization("Cardiologie");
        doctor.setDepartment("Cardiologie");

        when(doctorDao.findByDoctorNumber("DR001")).thenReturn(Optional.of(doctor));

        // Act
        ResponseEntity<DoctorDTO> response = doctorController.getDoctorByNumber("DR001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DR001", response.getBody().getDoctorNumber());
        verify(doctorDao, times(1)).findByDoctorNumber("DR001");
    }

    @Test
    public void testGetDoctorByNumber_NotFound() {
        // Arrange
        when(doctorDao.findByDoctorNumber("DR999")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<DoctorDTO> response = doctorController.getDoctorByNumber("DR999");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorDao, times(1)).findByDoctorNumber("DR999");
    }

    @Test
    public void testGetDoctorsBySpecialization() {
        // Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDoctorNumber("DR001");
        doctor1.setFirstName("Dr. Dupont");
        doctor1.setSpecialization("Cardiologie");
        doctor1.setDepartment("Cardiologie");

        Doctor doctor2 = new Doctor();
        doctor2.setId(3L);
        doctor2.setDoctorNumber("DR003");
        doctor2.setFirstName("Dr. Blanc");
        doctor2.setSpecialization("Cardiologie");
        doctor2.setDepartment("Cardiologie");

        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);

        when(doctorDao.findBySpecialization("Cardiologie")).thenReturn(doctors);

        // Act
        List<DoctorDTO> result = doctorController.getDoctorsBySpecialization("Cardiologie").getBody();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Cardiologie", result.get(0).getSpecialization());
        assertEquals("Cardiologie", result.get(1).getSpecialization());
        verify(doctorDao, times(1)).findBySpecialization("Cardiologie");
    }

    @Test
    public void testGetDoctorsByDepartment() {
        // Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDoctorNumber("DR001");
        doctor1.setFirstName("Dr. Dupont");
        doctor1.setSpecialization("Cardiologie");
        doctor1.setDepartment("Cardiologie");

        Doctor doctor2 = new Doctor();
        doctor2.setId(3L);
        doctor2.setDoctorNumber("DR003");
        doctor2.setFirstName("Dr. Blanc");
        doctor2.setSpecialization("Cardiologie");
        doctor2.setDepartment("Cardiologie");

        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);

        when(doctorDao.findByDepartment("Cardiologie")).thenReturn(doctors);

        // Act
        List<DoctorDTO> result = doctorController.getDoctorsByDepartment("Cardiologie");

        // Assert
        assertEquals(2, result.size());
        assertEquals("Cardiologie", result.get(0).getDepartment());
        assertEquals("Cardiologie", result.get(1).getDepartment());
        verify(doctorDao, times(1)).findByDepartment("Cardiologie");
    }

    @Test
    public void testSaveDoctor() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DR001");
        doctor.setLastName("Dupont");
        doctor.setFirstName("Dupond");
        doctor.setSpecialization("Cardiologie");
        doctor.setDepartment("Cardiologie");

        doNothing().when(doctorDao).save(doctor);

        // Act
        ResponseEntity<?> response = doctorController.saveDoctor(doctor);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(doctor, response.getBody());
        verify(doctorDao, times(1)).save(doctor);
    }

    @Test
    public void testDeleteDoctor() {
        // Arrange
        Long doctorId = 1L;
        doNothing().when(doctorDao).deleteById(doctorId);

        // Act
        doctorController.deleteDoctor(doctorId);

        // Assert
        verify(doctorDao, times(1)).deleteById(doctorId);
    }
}
