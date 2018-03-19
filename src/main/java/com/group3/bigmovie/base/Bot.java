package com.group3.bigmovie.base;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public final class Bot extends ListenerAdapter
{
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
            String path = currentRelativePath.toAbsolutePath().toString();

            CommandTranslator c = new CommandTranslator(
                  removeFirstMentions(event.getMessage().getContentDisplay()));
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

                        event.getChannel().sendFile(imageInByte, "Java, why....png").queue();
                  }
                  catch (IOException e)
                  {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                  }
            }
            else
            {
                  event.getChannel().sendMessage(response).queue();
            }

      }
}
