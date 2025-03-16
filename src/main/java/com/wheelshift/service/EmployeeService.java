package com.wheelshift.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wheelshift.model.Employee;
import com.wheelshift.model.Sale;
import com.wheelshift.repository.EmployeeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional
    public Employee saveEmployee(Employee employee) {
        // Encrypt password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
 
    @Transactional
    public Employee updateEmployee(Employee employee) {
        // Check if we need to update the password
        Optional<Employee> existingEmployee = employeeRepository.findById(employee.getId());
        if (existingEmployee.isPresent()) {
            if (employee.getPassword() != null && !employee.getPassword().equals(existingEmployee.get().getPassword())) {
                // New password provided, encrypt it
                employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            } else {
                // No new password, keep the existing one
                employee.setPassword(existingEmployee.get().getPassword());
            }
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
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
    
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
    
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByStatus("ACTIVE");
    }
    
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    public List<Employee> getEmployeesByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }
    
    public boolean isEmailUnique(String email) {
        return !employeeRepository.findByEmail(email).isPresent();
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

    @Transactional
    public void updateEmployeeStatus(Long id, String status) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setStatus(status);
            employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Employee not found with id: " + id);
        }
    }
    
    @Transactional
    public void updateEmployeePerformance(Long id, String performance) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setPerformance(performance);
            employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Employee not found with id: " + id);
        }
    }
    
    @Transactional
    public void updateLastLogin(Long id, LocalDateTime loginTime) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setLastLogin(loginTime);
            employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Employee not found with id: " + id);
        }
    }
    
    public boolean authenticateEmployee(String email, String password) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            return passwordEncoder.matches(password, employee.getPassword());
        }
        return false;
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

    public List<Employee> getTopSalesPeople(int limit) {
        return employeeRepository.findTopSalesPeople().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public Map<String, Long> getEmployeesByDepartmentStats() {
        Map<String, Long> departmentStats = new HashMap<>();
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            String department = employee.getDepartment();
            departmentStats.put(department, departmentStats.getOrDefault(department, 0L) + 1);
        }
        
        return departmentStats;
    }
    
    public Map<String, Long> getEmployeesByPositionStats() {
        Map<String, Long> positionStats = new HashMap<>();
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            String position = employee.getPosition();
            positionStats.put(position, positionStats.getOrDefault(position, 0L) + 1);
        }
        
        return positionStats;
    }
    
    public Map<String, Long> getEmployeesByStatusStats() {
        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("ACTIVE", employeeRepository.countByStatus("ACTIVE"));
        statusStats.put("INACTIVE", employeeRepository.countByStatus("INACTIVE"));
        statusStats.put("ON_LEAVE", employeeRepository.countByStatus("ON_LEAVE"));
        statusStats.put("TERMINATED", employeeRepository.countByStatus("TERMINATED"));
        return statusStats;
    }
    
    public double getAverageSalesPerEmployee() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            return 0.0;
        }
        
        int totalSales = 0;
        for (Employee employee : employees) {
            totalSales += employee.getHandledSales().size();
        }
        
        return (double) totalSales / employees.size();
    }
    
    public LocalDate getLastEmployeeHireDate() {
        return employeeRepository.findAll().stream()
                .map(Employee::getJoinDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }
    
    public Map<Long, Integer> getEmployeeSalesCount() {
        Map<Long, Integer> salesCount = new HashMap<>();
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            salesCount.put(employee.getId(), employee.getHandledSales().size());
        }
        
        return salesCount;
    }
    
    public Map<Long, Double> getEmployeeCommissionTotals() {
        Map<Long, Double> commissionTotals = new HashMap<>();
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            double totalCommission = 0.0;
            List<Sale> sales = employee.getHandledSales();
            
            for (Sale sale : sales) {
                if (sale.getTotalCommission() != null) {
                    totalCommission += sale.getTotalCommission().doubleValue();
                }
            }
            
            commissionTotals.put(employee.getId(), totalCommission);
        }
        
        return commissionTotals;
    }
}