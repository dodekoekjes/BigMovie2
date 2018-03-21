package com.group3.bigmovie;


import javax.security.auth.login.LoginException;

import com.group3.bigmovie.base.Bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;


final class App
{
   public static void main(String[] args)
      throws LoginException, InterruptedException
   {
      new JDABuilder(AccountType.BOT)
         .setToken(Settings.BOT_TOKEN)
         .addEventListener(new Bot())
         .buildBlocking();
      
   }
}
