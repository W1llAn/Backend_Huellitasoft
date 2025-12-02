package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.Schedule.ScheduleRequestDTO;
import huellitassoft_web.huellitasoft.dto.Schedule.ScheduleResponseDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryRequestDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryResponseDTO;
import huellitassoft_web.huellitasoft.entity.Subsidiary;
import huellitassoft_web.huellitasoft.entity.SubsidiarySchedule;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.DayOfWeek;
import huellitassoft_web.huellitasoft.enums.ShiftType;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SubsidiaryRepository;
import huellitassoft_web.huellitasoft.repository.SubsidiaryScheduleRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.impl.SubsidiaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubsidiaryService Integration Tests")
class SubsidiaryServiceIntegrationTest {

    @Mock
    private SubsidiaryRepository subsidiaryRepository;

    @Mock
    private SubsidiaryScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubsidiaryServiceImpl subsidiaryService;

    private Subsidiary testSubsidiary;
    private SubsidiaryRequestDTO subsidiaryRequestDTO;
    private User testManager;

    @BeforeEach
    void setUp() {
        testManager = User.builder()
                .idUsuario(1L)
                .email("manager@example.com")
                .username("manager")
                .contrasena("password")
                .rol(UserRol.ROLE_ADMINISTRADOR)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        testSubsidiary = new Subsidiary();
        testSubsidiary.setIdSubsidiary(1L);
        testSubsidiary.setName("Sucursal Central");
        testSubsidiary.setAddress("Calle Principal 123");
        testSubsidiary.setState(SubsidiaryState.ACTIVO);
        testSubsidiary.setManager(testManager);
        testSubsidiary.setSchedules(new ArrayList<>());

        subsidiaryRequestDTO = new SubsidiaryRequestDTO();
        subsidiaryRequestDTO.setName("Sucursal Central");
        subsidiaryRequestDTO.setAddress("Calle Principal 123");
        subsidiaryRequestDTO.setState(SubsidiaryState.ACTIVO);
        subsidiaryRequestDTO.setIdUsuario(1L);
    }

    @Test
    @DisplayName("Debe crear una sucursal exitosamente")
    void testCreateSubsidiary_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenReturn(testSubsidiary);

        // When
        SubsidiaryResponseDTO result = subsidiaryService.createSubsidiary(subsidiaryRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdSubsidiary()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sucursal Central");
        verify(subsidiaryRepository, times(1)).save(any(Subsidiary.class));
    }

    @Test
    @DisplayName("Debe obtener sucursal por ID exitosamente")
    void testGetSubsidiaryById_Success() {
        // Given
        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));

        // When
        SubsidiaryResponseDTO result = subsidiaryService.getSubsidiaryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdSubsidiary()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sucursal Central");
        verify(subsidiaryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar sucursal inexistente")
    void testGetSubsidiaryById_NotFound() {
        // Given
        when(subsidiaryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.getSubsidiaryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada");

        verify(subsidiaryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener todas las sucursales")
    void testGetAllSubsidiaries_Success() {
        // Given
        when(subsidiaryRepository.findAll()).thenReturn(Arrays.asList(testSubsidiary));

        // When
        List<SubsidiaryResponseDTO> result = subsidiaryService.getAllSubsidiaries();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sucursal Central");
        verify(subsidiaryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar sucursal exitosamente")
    void testUpdateSubsidiary_Success() {
        // Given
        SubsidiaryRequestDTO updateDTO = new SubsidiaryRequestDTO();
        updateDTO.setName("Sucursal Norte");
        updateDTO.setAddress("Avenida Norte 456");
        updateDTO.setState(SubsidiaryState.ACTIVO);
        updateDTO.setIdUsuario(1L);

        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenReturn(testSubsidiary);

        // When
        SubsidiaryResponseDTO result = subsidiaryService.updateSubsidiary(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(subsidiaryRepository, times(1)).save(any(Subsidiary.class));
    }

    @Test
    @DisplayName("Debe eliminar sucursal exitosamente")
    void testDeleteSubsidiary_Success() {
        // Given
        when(subsidiaryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(subsidiaryRepository).deleteById(1L);

        // When
        subsidiaryService.deleteSubsidiary(1L);

        // Then
        verify(subsidiaryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe obtener sucursales por estado")
    void testGetSubsidiariesByState_Success() {
        // Given
        when(subsidiaryRepository.findByState(SubsidiaryState.ACTIVO))
                .thenReturn(Arrays.asList(testSubsidiary));

        // When
        List<SubsidiaryResponseDTO> result = subsidiaryService.getSubsidiariesByState(SubsidiaryState.ACTIVO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo(SubsidiaryState.ACTIVO);
        verify(subsidiaryRepository, times(1)).findByState(SubsidiaryState.ACTIVO);
    }

    @Test
    @DisplayName("Debe obtener sucursales por gestor")
    void testGetSubsidiariesByManager_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(subsidiaryRepository.findByManager_IdUsuario(1L))
                .thenReturn(Arrays.asList(testSubsidiary));

        // When
        List<SubsidiaryResponseDTO> result = subsidiaryService.getSubsidiariesByManager(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(subsidiaryRepository, times(1)).findByManager_IdUsuario(1L);
    }

    @Test
    @DisplayName("Debe obtener sucursales por gestor y estado")
    void testGetSubsidiariesByManagerAndState_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(subsidiaryRepository.findByManager_IdUsuarioAndState(1L, SubsidiaryState.ACTIVO))
                .thenReturn(Arrays.asList(testSubsidiary));

        // When
        List<SubsidiaryResponseDTO> result = subsidiaryService.getSubsidiariesByManagerAndState(1L, SubsidiaryState.ACTIVO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo(SubsidiaryState.ACTIVO);
        verify(subsidiaryRepository, times(1)).findByManager_IdUsuarioAndState(1L, SubsidiaryState.ACTIVO);
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear sucursal con nombre duplicado")
    void testCreateSubsidiary_NameAlreadyExists() {
        // Given
        when(subsidiaryRepository.existsByName("Sucursal Central")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.createSubsidiary(subsidiaryRequestDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una sucursal con el nombre: Sucursal Central");

        verify(subsidiaryRepository, never()).save(any(Subsidiary.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear sucursal si el gestor no existe")
    void testCreateSubsidiary_ManagerNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.createSubsidiary(subsidiaryRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 1");

        verify(subsidiaryRepository, never()).save(any(Subsidiary.class));
    }

    @Test
    @DisplayName("Debe crear sucursal con horarios exitosamente")
    void testCreateSubsidiary_WithSchedules_Success() {
        // Given
        ScheduleRequestDTO schedule1 = new ScheduleRequestDTO();
        schedule1.setDayOfWeek(DayOfWeek.LUNES);
        schedule1.setShiftType(ShiftType.MATUTINO);
        schedule1.setStartTime(LocalTime.of(8, 0));
        schedule1.setEndTime(LocalTime.of(13, 0));
        schedule1.setIsOpen(true);

        ScheduleRequestDTO schedule2 = new ScheduleRequestDTO();
        schedule2.setDayOfWeek(DayOfWeek.MARTES);
        schedule2.setShiftType(ShiftType.VESPERTINO);
        schedule2.setStartTime(LocalTime.of(14, 0));
        schedule2.setEndTime(LocalTime.of(19, 0));
        schedule2.setIsOpen(true);

        subsidiaryRequestDTO.setSchedules(Arrays.asList(schedule1, schedule2));

        Subsidiary savedSubsidiary = new Subsidiary();
        savedSubsidiary.setIdSubsidiary(1L);
        savedSubsidiary.setName("Sucursal Nueva");
        savedSubsidiary.setSchedules(new ArrayList<>()); // será llenado después

        when(userRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenReturn(savedSubsidiary);
        when(scheduleRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        // When
        SubsidiaryResponseDTO result = subsidiaryService.createSubsidiary(subsidiaryRequestDTO);

        // Then
        assertThat(result.getSchedules()).hasSize(2);
        assertThat(result.getSchedules()).extracting(s -> s.getDayOfWeek().name())
                .containsExactlyInAnyOrder("LUNES", "MARTES");
        verify(scheduleRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe actualizar sucursal con nuevo nombre, gestor y horarios")
    void testUpdateSubsidiary_FullUpdate_Success() {
        // Given
        User newManager = User.builder().idUsuario(99L).username("newmanager").email("new@manager.com").build();

        ScheduleRequestDTO newSchedule = new ScheduleRequestDTO();
        newSchedule.setDayOfWeek(DayOfWeek.LUNES);
        newSchedule.setShiftType(ShiftType.MATUTINO);
        newSchedule.setStartTime(LocalTime.of(9, 0));
        newSchedule.setEndTime(LocalTime.of(18, 0));
        newSchedule.setIsOpen(true);

        SubsidiaryRequestDTO updateDTO = new SubsidiaryRequestDTO();
        updateDTO.setName("Sucursal Renovada");
        updateDTO.setAddress("Nueva dirección 789");
        updateDTO.setState(SubsidiaryState.ACTIVO);
        updateDTO.setIdUsuario(99L);
        updateDTO.setSchedules(Arrays.asList(newSchedule));

        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(userRepository.findById(99L)).thenReturn(Optional.of(newManager));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenAnswer(i -> i.getArgument(0));

        // When
        SubsidiaryResponseDTO result = subsidiaryService.updateSubsidiary(1L, updateDTO);

        // Then
        assertThat(result.getName()).isEqualTo("Sucursal Renovada");
        assertThat(result.getIdUsuario()).isEqualTo(99L);
        assertThat(result.getUsuarioUsername()).isEqualTo("newmanager");
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getStartTime()).isEqualTo("09:00");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo nombre ya existe (otra sucursal)")
    void testUpdateSubsidiary_NameAlreadyExists() {
        // Given
        SubsidiaryRequestDTO updateDTO = new SubsidiaryRequestDTO();
        updateDTO.setName("Sucursal Existente");

        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(subsidiaryRepository.existsByName("Sucursal Existente")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.updateSubsidiary(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una sucursal con el nombre");

        verify(subsidiaryRepository, never()).save(any(Subsidiary.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo gestor no existe")
    void testUpdateSubsidiary_ManagerNotFound() {
        // Given
        SubsidiaryRequestDTO updateDTO = new SubsidiaryRequestDTO();
        updateDTO.setIdUsuario(999L);

        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.updateSubsidiary(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");

        verify(subsidiaryRepository, never()).save(any(Subsidiary.class));
    }

    @Test
    @DisplayName("Debe eliminar horarios antiguos y agregar nuevos al actualizar")
    void testUpdateSubsidiary_ReplaceSchedules_Success() {
        // Given - Sucursal con un horario viejo
        SubsidiarySchedule oldSchedule = new SubsidiarySchedule();
        oldSchedule.setDayOfWeek(DayOfWeek.LUNES);
        testSubsidiary.getSchedules().add(oldSchedule);

        ScheduleRequestDTO newSchedule = new ScheduleRequestDTO();
        newSchedule.setDayOfWeek(DayOfWeek.VIERNES);
        newSchedule.setShiftType(ShiftType.MATUTINO);
        newSchedule.setStartTime(LocalTime.of(10, 0));
        newSchedule.setEndTime(LocalTime.of(17, 0));
        newSchedule.setIsOpen(true);

        SubsidiaryRequestDTO updateDTO = new SubsidiaryRequestDTO();
        updateDTO.setSchedules(Arrays.asList(newSchedule));

        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenAnswer(i -> i.getArgument(0));

        // When
        SubsidiaryResponseDTO result = subsidiaryService.updateSubsidiary(1L, updateDTO);

        // Then
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.VIERNES);
        assertThat(testSubsidiary.getSchedules()).hasSize(1);
    }

    @Test
    @DisplayName("Debe mantener horarios existentes si no se envían en el DTO")
    void testUpdateSubsidiary_KeepExistingSchedules() {
        // Given
        SubsidiarySchedule existing = new SubsidiarySchedule();
        existing.setDayOfWeek(DayOfWeek.SABADO);
        testSubsidiary.getSchedules().add(existing);

        SubsidiaryRequestDTO updateDTO = new SubsidiaryRequestDTO();
        updateDTO.setName("Sucursal Actualizada");
        // No se envían schedules → deben mantenerse

        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(subsidiaryRepository.save(any(Subsidiary.class))).thenAnswer(i -> i.getArgument(0));

        // When
        SubsidiaryResponseDTO result = subsidiaryService.updateSubsidiary(1L, updateDTO);

        // Then
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.SABADO);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar sucursales por gestor inexistente")
    void testGetSubsidiariesByManager_UserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.getSubsidiariesByManager(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar por gestor y estado si el gestor no existe")
    void testGetSubsidiariesByManagerAndState_UserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> subsidiaryService.getSubsidiariesByManagerAndState(999L, SubsidiaryState.ACTIVO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay sucursales")
    void testGetAllSubsidiaries_EmptyList() {
        // Given
        when(subsidiaryRepository.findAll()).thenReturn(List.of());

        // When
        List<SubsidiaryResponseDTO> result = subsidiaryService.getAllSubsidiaries();

        // Then
        assertThat(result).isEmpty();
    }
}
