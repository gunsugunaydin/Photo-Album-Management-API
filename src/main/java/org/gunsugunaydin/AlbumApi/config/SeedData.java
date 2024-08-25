package org.gunsugunaydin.AlbumApi.config;

import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAccountService;
import org.gunsugunaydin.AlbumApi.util.constants.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private IAccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        if(accountService.findAll().isEmpty()) {
            
            Account account01 = new Account();
            Account account02 = new Account();

            account01.setEmail("user@user.com");
            account01.setPassword("SomeValidPassword");
            account01.setAuthorities(Authority.USER.toString());
            accountService.save(account01);

            account02.setEmail("admin@admin.com");
            account02.setPassword("SomeValidPassword");
            account02.setAuthorities(Authority.ADMIN.toString() +" "+Authority.USER.toString() );
            accountService.save(account02);       
        }       
    }   
}
