package com.wheelshift.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wheelshift.dto.ClientSearchCriteria;
import com.wheelshift.model.Client;
import com.wheelshift.service.ClientService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
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
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        if (!clientService.isEmailUnique(client.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(client));
    }
    
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }
    
    @GetMapping("/paged")
    public ResponseEntity<Page<Client>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Client> clients = clientService.getAllClients(pageRequest);
        return ResponseEntity.ok(clients);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        client.setId(id);
        return ResponseEntity.ok(clientService.updateClient(client));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteClient(id);
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
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        Optional<Client> client = clientService.getClientByEmail(email);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Client>> searchClients(
            ClientSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    	PageRequest pageRequest = PageRequest.of(page, size);
        Page<Client> clients = clientService.searchClients(criteria, pageRequest);
        return ResponseEntity.ok(clients);
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
    
    @GetMapping("/active")
    public ResponseEntity<List<Client>> getActiveClients() {
        return ResponseEntity.ok(clientService.getActiveClients());
    }
    
    @GetMapping("/inactive")
    public ResponseEntity<List<Client>> getInactiveClients() {
        return ResponseEntity.ok(clientService.getInactiveClients());
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateClientStatus(@PathVariable Long id, @RequestParam String status) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.updateClientStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/increment-purchases")
    public ResponseEntity<Void> incrementClientPurchases(@PathVariable Long id) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.incrementClientPurchases(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/last-purchase")
    public ResponseEntity<Void> updateLastPurchaseDate(@PathVariable Long id, @RequestParam LocalDate purchaseDate) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.updateLastPurchaseDate(id, purchaseDate);
        return ResponseEntity.ok().build();
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
    
    @GetMapping("/stats/top-buyers")
    public ResponseEntity<List<Client>> getTopBuyers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(clientService.getTopBuyers(limit));
    }
    
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getTotalClientCount() {
        return ResponseEntity.ok(clientService.getTotalClientCount());
    }
    
    @GetMapping("/stats/new-clients")
    public ResponseEntity<Long> getNewClientsInPeriod(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(clientService.getNewClientsInPeriod(startDate, endDate));
    }
    
    @GetMapping("/stats/by-status")
    public ResponseEntity<Map<String, Long>> getClientsByStatus() {
        return ResponseEntity.ok(clientService.getClientsByStatus());
    }
    
    @GetMapping("/stats/by-location")
    public ResponseEntity<Map<String, Long>> getClientsByLocation() {
        return ResponseEntity.ok(clientService.getClientsByLocation());
    }
    
    @GetMapping("/stats/average-purchases")
    public ResponseEntity<Double> getAveragePurchasesPerClient() {
        return ResponseEntity.ok(clientService.getAveragePurchasesPerClient());
    }
    
    @GetMapping("/stats/last-activity")
    public ResponseEntity<LocalDate> getLastClientActivity() {
        LocalDate lastActivity = clientService.getLastClientActivity();
        return lastActivity != null ? ResponseEntity.ok(lastActivity) : ResponseEntity.noContent().build();
    }
}