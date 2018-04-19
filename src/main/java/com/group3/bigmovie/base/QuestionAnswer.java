package com.group3.bigmovie.base;


import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.logging.Logger;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Marker;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class QuestionAnswer
{
    private final Logger LOGGER = LogManager.getLogger(this);

    private String m_meaning;
    private String m_name;


    // Combine the String arrays from Commandtranslator into singke strings.
    public QuestionAnswer(CommandTranslator c)
    {
        m_meaning = String.join(" ", c.getMeaning());
        m_name = String.join(" ", c.getname());
    }

    // Send query to R based on the meaning of the question.
    public String answerQuestion()
    {
        int type = questionType();
        String q;
        String queryResult;

        switch (type)
        {
        case Meaning.TEST:
            return "I am Henk.";

        case Meaning.HELLO:
            return "Hello there.";

        case Meaning.WHY_HERE:
            return "That's... why i'm here";

        case Meaning.HELP:
            return Meaning.STR_HELP;

        case Meaning.AGE:
            q = "SELECT `birthYear` FROM `name` WHERE `primaryName` = " + m_name;

            queryResult = String.join(", ", callR(q, false));
            return m_name + " is " + queryResult + " years old.";

        case Meaning.AMOUNT_DIRECTED:
            q = "SELECT COUNT(`title`.`primaryTitle`) as `movies_directed`, `title`.`primaryTitle` " +
                "FROM `name` " +
                "WHERE `primaryName` = " + m_name +
                "INNER JOIN `name_profession` as `np`" +
                "ON `name`.`id` = `np`.`name_id` AND `np`.`profession` = 'director' " +
                "INNER JOIN `name_known_for_titles` as `nkft`" +
                "ON `nkft`.`name_id` = `np`.`name_id`" +
                "INNER JOIN `title`" +
                "ON `title`.`id` = `nkft`.`title_id`";

            queryResult = String.join(", ", callR(q, false));
            return m_name + " has directed " + queryResult + " movies and/or series.";

        case Meaning.AMOUNT_PLAYED:
            q = "SELECT COUNT(`title`.`primaryTitle`) as `played_in`, `title`.`primaryTitle` " +
                "FROM `name` " +
                "WHERE `primaryName` = " + m_name +
                "INNER JOIN `name_profession` as `np`" +
                "ON `name`.`id` = `np`.`name_id` AND `np`.`profession` = 'actor' OR `np`.`profession` = `actress`" +
                "INNER JOIN `name_known_for_titles` as `nkft`" +
                "ON `nkft`.`name_id` = `np`.`name_id`" +
                "INNER JOIN `title`" +
                "ON `title`.`id` = `nkft`.`title_id`";

            queryResult = String.join(", ", callR(q, false));
            return m_name + " has played in " + queryResult + " movies and/or series.";

        case Meaning.WHEN_RELEASED:
            q = "SELECT `startYear` " +
                "FROM `title` " +
                "WHERE `primaryTitle` = " + m_name + " OR `originalTitle` = " + m_name;

            queryResult = String.join(", ", callR(q, false));
            return m_name + " was released <Date>.";

        case Meaning.WHO_PLAYED:
            q = "SELECT `name`.`primaryName`, `np`.`profession` " +
                "FROM `title`" +
                "WHERE `primaryTitle` = " + m_name +
                "OR `originalTitle` = " + m_name +
                "INNER JOIN `name_known_for_titles` as `nkft`" +
                "ON `nkft`.`title_id` = `title`.`id`" +
                "INNER JOIN `name_profession` as `np`" +
                "ON `np`.`name_id` = `nkft`.`name_id`" +
                "INNER JOIN `name`" +
                "ON `name`.`id` = `np`.`name_id`";

            queryResult = String.join(", ", callR(q, false));
            return queryResult + " played in " + m_name + ".";

        case Meaning.WHO_WROTE:
            q = "SELECT `name`.`primaryName` " +
                "FROM `title`" +
                "WHERE `primaryTitle` = " + m_name +
                "OR `originalTitle` = " + m_name +
                "INNER JOIN `name_known_for_titles` as `nkft`" +
                "ON `nkft`.`title_id` = `title`.`id`" +
                "INNER JOIN `name_profession` as `np`" +
                "ON `np`.`name_id` = `nkft`.`name_id`" +
                "AND `np`.`profession` = 'writer'" +
                "INNER JOIN `name`" +
                "ON `name`.`id` = `np`.`name_id`";

            queryResult = String.join(", ", callR(q, false));
            return m_name + " was written by <Writer>.";

        case Meaning.RECOMMEND:
            int random = ThreadLocalRandom.current().nextInt(0, 7344821 + 1);

            q = 
                "SELECT `originalTitle` " +
                "FROM `title` " +
                "WHERE `id` = "+random+" " +
                "LIMIT 1;";

            queryResult = String.join(", ", callR(q, false));
            return "I think you would like " + queryResult + ".";

        case Meaning.WHAT_ABOUT:
            q = "EMPTY QUERY";

            queryResult = String.join(", ", callR(q, false));
            return m_name + ": " + queryResult;


        case Meaning.WHAT_RATING:
            q = 
                "SELECT `rating`.`averageRating`" +
                "FROM `rating`" +
                "WHERE `title`.`originalTitle` = " + m_name +
                "INNER JOIN `title`" +
                "ON `rating`.`title_id` = `title`.`id`";
            
            queryResult = String.join(", ", callR(q, false));
            return m_name + " has an average rating of " + queryResult + ".";

        case Meaning.ABOVE_AGE:
            q = 
                "SELECT `count(np.name_id)` as `am_actors`" +
                "FROM `name_profession` as `np`" +
                "INNER JOIN `name`" +
                "ON `name`.`id` = `am_actors`.`name_id`" +
                "AND `name`.`birthYear` > " + m_name;

            queryResult = String.join(", ", callR(q, false));
            return queryResult + "actors and actresses are above the age of " + m_name + ".";
            
        case Meaning.WHAT_IS:
            m_name = m_name.replace(" ", "+");

            return "http://lmgtfy.com/?q=" + m_name;

        case Meaning.WHAT_IS_LOVE:
            return "BABY DON'T HURT ME!\n" + "https://www.youtube.com/watch?v=HEXWRTEbj1I";

        case Meaning.ACTOR_TO_ACTRESS:
            q = 
                "SELECT `count(name_id)` as `am_actors` " +
                "FROM `name_profession` " +
                "WHERE `profession` = ‘actor’" +
                "SELECT `count(name_id)` as `am_actors` " +
                "FROM `name_profession` " +
                "WHERE `profession` = ‘actress’";

            queryResult = String.join(", ", callR(q, true));
            return "IMAGE";            
            
        case Meaning.SEARCH_YT:
            return "SEARCH_YT";
        
        default:
            return "RIVE";
        }
    }

    // Convert the meaning of the question to an integer.
    private int questionType()
    {
        System.out.println(m_meaning);

        if (m_meaning.equalsIgnoreCase("how old"))
        {
            return Meaning.AGE;
        }
        else if (m_meaning.equalsIgnoreCase("test"))
        {
            return Meaning.TEST;
        }
        else if (m_meaning.equalsIgnoreCase("can help") ||
            m_meaning.equalsIgnoreCase("help"))
        {
            return Meaning.HELP;
        }
        else if (m_meaning.equalsIgnoreCase("hello") ||
            m_meaning.equalsIgnoreCase("hi"))
        {
            return Meaning.HELLO;
        }
        else if (m_meaning.equalsIgnoreCase("can answer question") ||
            m_meaning.equalsIgnoreCase("can answer questions"))
        {
            return Meaning.WHY_HERE;
        }
        else if (m_meaning.equalsIgnoreCase("how many directed"))
        {
            return Meaning.AMOUNT_DIRECTED;
        }
        else if (m_meaning.equalsIgnoreCase("how many played"))
        {
            return Meaning.AMOUNT_PLAYED;
        }
        else if (m_meaning.equalsIgnoreCase("when released"))
        {
            return Meaning.WHEN_RELEASED;
        }
        else if (m_meaning.equalsIgnoreCase("where filmed") ||
            m_meaning.equalsIgnoreCase("where recorded"))
        {
            return Meaning.WHERE_FILMED;
        }
        else if (m_meaning.equalsIgnoreCase("what budget"))
        {
            return Meaning.WHAT_BUDGET;
        }
        else if (m_meaning.equalsIgnoreCase("who plays") ||
            m_meaning.equalsIgnoreCase("who played"))
        {
            return Meaning.WHO_PLAYED;
        }
        else if (m_meaning.equalsIgnoreCase("who wrote"))
        {
            return Meaning.WHO_WROTE;
        }
        else if (m_meaning.equalsIgnoreCase("what about") ||
            m_meaning.equalsIgnoreCase("describe"))
        {
            return Meaning.WHAT_ABOUT;
        }
        else if (m_meaning.equalsIgnoreCase("what rating") || m_meaning.equalsIgnoreCase("how many stars"))
        {
            return Meaning.WHAT_RATING;
        }
        else if (m_meaning.equalsIgnoreCase("what ratio"))
        {
            return Meaning.ACTOR_TO_ACTRESS;
        }
        else if (m_meaning.equalsIgnoreCase("above age"))
        {
            return Meaning.ABOVE_AGE;
        }
        else if (m_meaning.equalsIgnoreCase("recommend"))
        {
            return Meaning.RECOMMEND;
        }
        else if (m_meaning.equalsIgnoreCase("what"))
        {
            return Meaning.WHAT_IS;
        }
        else if (m_meaning.equalsIgnoreCase("what love"))
        {
            return Meaning.WHAT_IS_LOVE;
        }
        else if (m_meaning.contains("youtube") || m_name.contains("youtube")){ //always place at the bottom of if/else if statements
            return Meaning.SEARCH_YT;
        }

        return -1;
    }

    // Returns the R query result(s).
    private String[] callR(String query, boolean r)
    {
        RConnection connection = null;

        String s = "";

        try
        {
            connection = new RConnection();

            Path currentRelativePath = Paths.get("src/main/r/test.r");
            s = currentRelativePath.toAbsolutePath().toString().replace('\\', '/');

            connection.eval("source('" + s + "')");

            System.out.println();
            //return connection.eval("meaningToQ(" + query + ", " + r + ")").asStrings(); // Gaat stuk D:
            REXP rResponseObject;
				try
				{
                    System.out.println(query);

                    String stringetje = "try(eval(meaningToQ(\""+query+"\")),silent=TRUE)";

					rResponseObject = connection.parseAndEval(
                        // "try(eval(testF()),silent=TRUE)");
                        stringetje
                    );

                        System.out.println(rResponseObject.asString());
                    if (rResponseObject.inherits("try-error")) {
                        LOGGER.error("R Serve Eval Exception : "+rResponseObject.asString());
                    }
                    return rResponseObject.asStrings();
				}
				catch (REngineException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            
        }
        catch (RserveException ex)
        {
            ex.printStackTrace();
        }
        catch (REXPMismatchException ex)
        {
            ex.printStackTrace();
            System.out.println("--- REXPMismatchException ---");
        }
        finally
        {
            connection.close();
        }

        return new String[]
        { "<INVALID>" };
    }
}
