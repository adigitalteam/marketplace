package com.example.config;

import com.example.entity.Language;
import com.example.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;


@Component("messageSource")
public class DBMessageSource extends AbstractMessageSource {

    @Value("${languages.primaryLang}")
    String primaryLang;

    @Autowired
    LanguageService languageService;

    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
        Language message = languageService.getOrCreate(key,locale.getLanguage());
        return new MessageFormat(message.getContent(), locale);
    }
}