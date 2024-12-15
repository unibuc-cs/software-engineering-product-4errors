package com._errors.MovieMingle.service.email;

import com._errors.MovieMingle.service.email.context.AbstractEmailContext;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendMail(final AbstractEmailContext email) throws MessagingException;
}
