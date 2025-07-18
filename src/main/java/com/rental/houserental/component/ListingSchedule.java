package com.rental.houserental.component;

import com.rental.houserental.entity.Listing;
import com.rental.houserental.enums.ListingStatus;
import com.rental.houserental.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListingSchedule {

    private final ListingRepository listingRepository;
    @Scheduled(cron = "0 0/30 * * * ?")
    public void updateAllListingStatus() {
        List<Listing> listings = listingRepository.findAll();
        for (Listing listing : listings) {
            updateListingStatus(listing);
        }
    }

    public void updateListingStatus(Listing listing) {
        if (!listing.getStatus().name().equalsIgnoreCase(ListingStatus.HIDDEN.name())) {
            if(listing.getStartDate().isBefore(LocalDateTime.now()) && listing.getEndDate().isAfter(LocalDateTime.now())) {
                listing.setStatus(ListingStatus.ACTIVE);
            } else if (listing.getStartDate().isAfter(LocalDateTime.now())) {
                listing.setStatus(ListingStatus.UPCOMING);
            } else {
                listing.setStatus(ListingStatus.EXPIRED);
            }
            listingRepository.save(listing);
        }
    }
}
