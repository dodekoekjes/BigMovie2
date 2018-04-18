package com.group3.bigmovie.base;


import com.group3.bigmovie.extensions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import com.rivescript.*;


public final class Bot extends ListenerAdapter
{
      RiveScript rive = new RiveScript(Config.utf8());

      public Bot(){
            //get rivescript resources
            Path currentRelativePath = Paths.get("src/main/resources/rivescript");
            String path = currentRelativePath.toAbsolutePath().toString().replace('\\', '/');
            System.out.println(path);
            rive.loadDirectory(path);
            rive.sortReplies();
      }
      // Only respond if given input returns true.
      private boolean shouldRespondTo(Message message)
      {
            // Don't respond to own messages.
            if (message.getAuthor().equals(message.getJDA().getSelfUser()))
            {
                  return false;
            }

            // Direct message?
            if (message.getChannelType() == ChannelType.PRIVATE)
            {
                  return true;
            }

            // Is mentioned?
            if (message.isMentioned(message.getJDA().getSelfUser()))
            {
                  return true;
            }

            return false;
      }

      // Remove a discord mention. (@USERNAME)
      private String removeFirstMentions(String text)
      {
            int index = 0;
            while (text.charAt(index) == '@')
            {
                  index = text.indexOf(' ', index) + 1;
                  if (index >= text.length())
                  {
                        index = 0;
                        break;
                  }
            }
            return text.substring(index);
      }

      @Override
      public void onReady(ReadyEvent event)
      {
            System.out.printf("Logged in as %s\n",
                  event.getJDA().getSelfUser().getName());
      }

      // Return the proper response to a question, returns a graph if the return
      // string is equal to "IMAGE".
      @Override
      public void onMessageReceived(MessageReceivedEvent event)
      {
            if (!shouldRespondTo(event.getMessage()))
            {
                  return;
            }

            Path currentRelativePath = Paths.get("src/main/r/test.png");
            String path = currentRelativePath.toAbsolutePath().toString().replace('\\', '/');
            String input = removeFirstMentions(event.getMessage().getContentDisplay());
            CommandTranslator c = new CommandTranslator(input);
            c.setMeaning();

            QuestionAnswer q = new QuestionAnswer(c);
            String response = q.answerQuestion();

            if (response.equals("IMAGE"))
            {
                  try
                  {
                        BufferedImage originalImage = ImageIO.read(new File(path));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(originalImage, "png", baos);
                        baos.flush();
                        byte[] imageInByte = baos.toByteArray();
                        baos.close();
                        System.out.println("[IMAGE] " + response);
                        event.getChannel().sendFile(imageInByte, "Java, why....png").queue();
                  }
                  catch (IOException e)
                  {
                        e.printStackTrace();
                  }
            }
            else if(response.equals("SEARCH_YT")){
                  SearchYT search = new SearchYT();
                  input = input.toLowerCase();
                  input = input.replace("search", "");
                  input = input.replace("youtube", "");
                  event.getChannel().sendMessage("You searched for \"" + input.trim() + "\"").queue();
                  search.setInputQuery(input);
                  search.execute();
                  List<String> results = search.getResults();
                  int i = 0;
                  System.out.println("[YOUTUBE]");
                  for(String result : results){
                        event.getChannel().sendMessage(result).queue();
                        i++;
                        System.out.println("[" + i + "] " + "URL: " + result);
                  }
            }
            else if(response.equals("RIVE")){
                  response = rive.reply(event.getChannel().getId(), removeFirstMentions(event.getMessage().getContentDisplay()));
                  System.out.println("[RIVE] " + response);
                  event.getChannel().sendMessage(response).queue();
            }
            else if(response.equals("BATMAN")){
                  response = rive.reply(event.getChannel().getId(), removeFirstMentions(event.getMessage().getContentDisplay()));
                  System.out.println("[BATMAN] " + response);
                  event.getGuild().getController().setNickname(
                        event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong()), "BATMAN");
                  System.out.println(event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong()).getEffectiveName());
                  event.getChannel().sendMessage(response).queue();
            }
            else
            {
                  System.out.println("[NORMAL] " + response);
                  event.getChannel().sendMessage(response).queue();
            }

      }
}
