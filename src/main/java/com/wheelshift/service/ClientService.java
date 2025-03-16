package com.wheelshift.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.wheelshift.model.Client;
import com.wheelshift.repository.ClientRepository;
import jakarta.transaction.Transactional;

@Service
public class ClientService {
    
	private final ClientRepository clientRepository;
	
	public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    @Transactional
    public Client updateClient(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
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
    
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
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

    public List<Client> getActiveClients() {
        return clientRepository.findByStatus("ACTIVE");
    }

    public List<Client> getInactiveClients() {
        return clientRepository.findByStatus("INACTIVE");
    }
    
    public boolean isEmailUnique(String email) {
        return !clientRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void updateClientStatus(Long id, String status) {
        Optional<Client> clientOpt = clientRepository.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setStatus(status);
            clientRepository.save(client);
        } else {
            throw new RuntimeException("Client not found with id: " + id);
        }
    }

    @Transactional
    public void incrementClientPurchases(Long id) {
        Optional<Client> clientOpt = clientRepository.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setTotalPurchases(client.getTotalPurchases() + 1);
            clientRepository.save(client);
        } else {
            throw new RuntimeException("Client not found with id: " + id);
        }
    }

    @Transactional
    public void updateLastPurchaseDate(Long id, LocalDate purchaseDate) {
        Optional<Client> clientOpt = clientRepository.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setLastPurchase(purchaseDate);
            clientRepository.save(client);
        } else {
            throw new RuntimeException("Client not found with id: " + id);
        }
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

    public List<Client> getTopBuyers(int limit) {
        return clientRepository.findTopBuyers().stream()
                .limit(limit)
                .toList();
    }

    public long getTotalClientCount() {
        return clientRepository.count();
    }

    public long getNewClientsInPeriod(LocalDate startDate, LocalDate endDate) {
        long totalBefore = clientRepository.countNewClientsAfter(startDate.minusDays(1));
        long totalAfter = clientRepository.countNewClientsAfter(endDate);
        return totalBefore - totalAfter;
    }

    public Map<String, Long> getClientsByStatus() {
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("ACTIVE", clientRepository.countByStatus("ACTIVE"));
        statusMap.put("INACTIVE", clientRepository.countByStatus("INACTIVE"));
        statusMap.put("LEAD", clientRepository.countByStatus("LEAD"));
        statusMap.put("VIP", clientRepository.countByStatus("VIP"));
        return statusMap;
    }
    
    public Map<String, Long> getClientsByLocation() {
        Map<String, Long> locationMap = new HashMap<>();
        List<Client> clients = clientRepository.findAll();
        
        for (Client client : clients) {
            String location = client.getLocation() != null ? client.getLocation() : "Unknown";
            locationMap.put(location, locationMap.getOrDefault(location, 0L) + 1);
        }
        
        return locationMap;
    }
    
    public double getAveragePurchasesPerClient() {
        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            return 0.0;
        }
        
        int totalPurchases = clients.stream()
                .mapToInt(Client::getTotalPurchases)
                .sum();
                
        return (double) totalPurchases / clients.size();
    }

    public LocalDate getLastClientActivity() {
        return clientRepository.findAll().stream()
                .filter(client -> client.getLastPurchase() != null)
                .map(Client::getLastPurchase)
                .max(LocalDate::compareTo)
                .orElse(null);
    }
}