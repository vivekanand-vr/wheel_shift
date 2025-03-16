package com.wheelshift.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wheelshift.model.Employee;
import com.wheelshift.service.EmployeeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
	 *	   _____ _____  _    _ _____  
	 *	  / ____|  __ \| |  | |  __ \ 
	 *	 | |    | |__) | |  | | |  | |
	 *	 | |    |  _  /| |  | | |  | |
	 *	 | |____| | \ \| |__| | |__| |
	 *	  \_____|_|  \_\\____/|_____/ 
	 *	                                                   
     *				CRUD OPERATIONS
     */
    
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        if (!employeeService.isEmailUnique(employee.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmployee(employee));
    }
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        if (!employeeService.getEmployeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employee.setId(id);
        return ResponseEntity.ok(employeeService.updateEmployee(employee));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        if (!employeeService.getEmployeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
	 *	   _____ ______          _____   _____ _    _ 
	 *	  / ____|  ____|   /\   |  __ \ / ____| |  | |
	 *	 | (___ | |__     /  \  | |__) | |    | |__| |
	 *	  \___ \|  __|   / /\ \ |  _  /| |    |  __  |
	 *	  ____) | |____ / ____ \| | \ \| |____| |  | |
	 *	 |_____/|______/_/    \_\_|  \_\\_____|_|  |_|
	 *	                                              
	 *				SEARCH & FILTERS OPERATIONS
     */
    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
        Optional<Employee> employee = employeeService.getEmployeeByEmail(email);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Employee>> getActiveEmployees() {
        return ResponseEntity.ok(employeeService.getActiveEmployees());
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department));
    }
    
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Employee>> getEmployeesByPosition(@PathVariable String position) {
        return ResponseEntity.ok(employeeService.getEmployeesByPosition(position));
    }
    
    /**
	 *	  ____  _    _  _____ _____ _   _ ______  _____ _____   _      ____   _____ _____ _____ 
	 *	 |  _ \| |  | |/ ____|_   _| \ | |  ____|/ ____/ ____| | |    / __ \ / ____|_   _/ ____|
	 *	 | |_) | |  | | (___   | | |  \| | |__  | (___| (___   | |   | |  | | |  __  | || |     
	 *	 |  _ <| |  | |\___ \  | | | . ` |  __|  \___ \\___ \  | |   | |  | | | |_ | | || |     
	 *	 | |_) | |__| |____) |_| |_| |\  | |____ ____) |___) | | |___| |__| | |__| |_| || |____ 
	 * 	 |____/ \____/|_____/|_____|_| \_|______|_____/_____/  |______\____/ \_____|_____\_____|
     *                                                                                   
     *				BUSINESS LOGIC & TRANSACTIONS                                                                                   
     */

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateEmployeeStatus(@PathVariable Long id, @RequestParam String status) {
        if (!employeeService.getEmployeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.updateEmployeeStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/performance")
    public ResponseEntity<Void> updateEmployeePerformance(@PathVariable Long id, @RequestParam String performance) {
        if (!employeeService.getEmployeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.updateEmployeePerformance(id, performance);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable Long id) {
        if (!employeeService.getEmployeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.updateLastLogin(id, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticateEmployee(@RequestParam String email, @RequestParam String password) {
        boolean isAuthenticated = employeeService.authenticateEmployee(email, password);
        return ResponseEntity.ok(isAuthenticated);
    }
    
    /**
     *  	____ _______    _______ _____ 
	 *	  / ____|__   __|/\|__   __/ ____|
	 *	 | (___    | |  /  \  | | | (___  
	 *	  \___ \   | | / /\ \ | |  \___ \ 
	 *	  ____) |  | |/ ____ \| |  ____) |
	 *	 |_____/   |_/_/    \_\_| |_____/ 
	 *
	 *				STATISTICS AND ANALYTICS
     */
    
    @GetMapping("/stats/top-sales")
    public ResponseEntity<List<Employee>> getTopSalesPeople(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(employeeService.getTopSalesPeople(limit));
    }
    
    @GetMapping("/stats/by-department")
    public ResponseEntity<Map<String, Long>> getEmployeesByDepartmentStats() {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartmentStats());
    }
    
    @GetMapping("/stats/by-position")
    public ResponseEntity<Map<String, Long>> getEmployeesByPositionStats() {
        return ResponseEntity.ok(employeeService.getEmployeesByPositionStats());
    }
    
    @GetMapping("/stats/by-status")
    public ResponseEntity<Map<String, Long>> getEmployeesByStatusStats() {
        return ResponseEntity.ok(employeeService.getEmployeesByStatusStats());
    }
    
    @GetMapping("/stats/average-sales")
    public ResponseEntity<Double> getAverageSalesPerEmployee() {
        return ResponseEntity.ok(employeeService.getAverageSalesPerEmployee());
    }
    
    @GetMapping("/stats/last-hire")
    public ResponseEntity<LocalDate> getLastEmployeeHireDate() {
        LocalDate lastHireDate = employeeService.getLastEmployeeHireDate();
        return lastHireDate != null ? ResponseEntity.ok(lastHireDate) : ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stats/sales-count")
    public ResponseEntity<Map<Long, Integer>> getEmployeeSalesCount() {
        return ResponseEntity.ok(employeeService.getEmployeeSalesCount());
    }
    
    @GetMapping("/stats/commission-totals")
    public ResponseEntity<Map<Long, Double>> getEmployeeCommissionTotals() {
        return ResponseEntity.ok(employeeService.getEmployeeCommissionTotals());
    }
}