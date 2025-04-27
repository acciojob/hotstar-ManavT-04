package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){
        // Fetch the user based on userId from the DTO
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElse(null);
        if (user == null) return -1;

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        // Calculate cost based on subscription type and number of screens required.
        int screens = subscriptionEntryDto.getNoOfScreensRequired();
        int totalCost = 0;
        if (subscriptionEntryDto.getSubscriptionType() == SubscriptionType.BASIC) {
            totalCost = 500 + 200 * screens;
        } else if (subscriptionEntryDto.getSubscriptionType() == SubscriptionType.PRO) {
            totalCost = 800 + 250 * screens;
        } else if (subscriptionEntryDto.getSubscriptionType() == SubscriptionType.ELITE) {
            totalCost = 1000 + 350 * screens;
        }
        subscription.setTotalAmountPaid(totalCost);
        // Record the number of screens subscribed in the subscription object.
        subscription.setNoOfScreensSubscribed(screens);

        // Set the subscription start date to current date.
        subscription.setStartSubscriptionDate(new Date());

        // Save the subscription and then update the user.
        subscriptionRepository.save(subscription);
        user.setSubscription(subscription);
        userRepository.save(user);

        return subscription.getId();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        // Fetch the user and verify that they have an existing subscription.
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) return -1;

        Subscription subscription = user.getSubscription();
        SubscriptionType currentType = subscription.getSubscriptionType();
        int screens = subscription.getNoOfScreensSubscribed();
        int currentCost = subscription.getTotalAmountPaid();
        int upgradeCost = 0;

        // Upgrade logic based on current subscription type.
        if (currentType == SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            // Calculate the cost difference for upgrading from BASIC to PRO.
            int newCost = 800 + 250 * screens;
            upgradeCost = newCost - (500 + 200 * screens);
        } else if (currentType == SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            // Calculate the cost difference for upgrading from PRO to ELITE.
            int newCost = 1000 + 350 * screens;
            upgradeCost = newCost - (800 + 250 * screens);
        } else {
            // For ELITE, no upgrade is possible.
            throw new Exception("Already at highest subscription level");
        }

        // Update the total amount paid to include the upgrade cost.
        subscription.setTotalAmountPaid(currentCost + upgradeCost);
        subscriptionRepository.save(subscription);

        return subscription.getId();
    }

    public Integer calculateTotalRevenueOfHotstar(){
        // Calculate total revenue from all subscriptions.
        return subscriptionRepository.findAll().stream()
                .mapToInt(Subscription::getTotalAmountPaid)
                .sum();
    }

}
