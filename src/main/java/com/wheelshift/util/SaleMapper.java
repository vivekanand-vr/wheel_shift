package com.wheelshift.util;

import com.wheelshift.dto.SaleDTO;
import com.wheelshift.model.Sale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to map Sale entities to SaleDTO objects
 */
public class SaleMapper {

   /**
    * Maps a Sale entity to a SaleDTO
    */
	
	private SaleMapper() {
		// Private constructor to hide the implicit public one.
	}
	
    public static SaleDTO toDTO(Sale sale) {
        if (sale == null) {
            return null;
        }
        
        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());
        
        // Map car details
        if (sale.getCar() != null) {
            dto.setCarId(sale.getCar().getId());
            dto.setCarMake(sale.getCar().getCarModel().getMake());
            dto.setCarModel(sale.getCar().getCarModel().getModel());
            dto.setCarYear(sale.getCar().getYear());
        }
        
        // Map client details
        if (sale.getClient() != null) {
            dto.setClientId(sale.getClient().getId());
            dto.setClientName(sale.getClient().getName());
            dto.setClientEmail(sale.getClient().getEmail());
        }
        
        // Map employee details
        if (sale.getHandledBy() != null) {
            dto.setEmployeeId(sale.getHandledBy().getId());
            dto.setEmployeeFullName(sale.getHandledBy().getName());
        }
        
        // Map sale details
        dto.setSaleDate(sale.getSaleDate());
        dto.setSalePrice(sale.getSalePrice());
        dto.setCommissionRate(sale.getCommissionRate());
        dto.setTotalCommission(sale.getTotalCommission());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setSaleDocumentsUrl(sale.getSaleDocumentsUrl());
        
        return dto;
    }
    
    /**
     * Maps a list of Sale entities to a list of SaleDTOs
     */
    public static List<SaleDTO> toDTOList(List<Sale> sales) {
        return sales.stream()
                .map(SaleMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Maps a Page of Sale entities to a Page of SaleDTOs
     */
    public static Page<SaleDTO> toDTOPage(Page<Sale> salePage) {
        List<SaleDTO> dtos = salePage.getContent().stream()
                .map(SaleMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, salePage.getPageable(), salePage.getTotalElements());
    }
}