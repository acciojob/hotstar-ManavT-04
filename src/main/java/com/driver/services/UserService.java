package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) return 0;

        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();
        Integer userAge = user.getAge();

        return (int) webSeriesRepository.findAll().stream()
                .filter(series -> series.getAgeLimit() <= userAge &&
                        (subscriptionType == SubscriptionType.ELITE ||
                                (subscriptionType == SubscriptionType.BASIC && series.getSubscriptionType() == SubscriptionType.BASIC)))
                .count();
    }


}
