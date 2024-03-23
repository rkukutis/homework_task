package com.rhoopoe.myfashiontrunk.exception;

import com.rhoopoe.myfashiontrunk.entity.Category;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class ProhibitedItemException extends RuntimeException{

    private final Set<Category> matchedProhibitedCategories;

    public ProhibitedItemException(String message, Set<Category> matchedProhibitedCategories) {
        super(message);
        this.matchedProhibitedCategories = matchedProhibitedCategories;
    }
}
