package com.group3.bigmovie.base;

import java.util.ArrayList;

public class CommandTranslator
{
    private String m_input;
    
    private ArrayList<String> m_meaning;
    private ArrayList<String> m_name;

    private final String[] m_meanings;
    private final String[] m_garbageWords;


    public CommandTranslator(String input)
    {
        m_input = input;

        m_meaning = new ArrayList<String>();
        m_name = new ArrayList<String>();

        // Words that carry a meaning.
        m_meanings = new String[]
        {
            "how", "test", "what", "where", "when", "many", "old", "played", "filmed", "recorded", "describe",
            "budget", "release", "released", "plays", "wrote", "about", "recommend", "directed", "who",
            "rating", "stars", "ratio", "above", "hello", "hi", "can", "answer", "questions", "question",
            "help", "love"
        };

        // Words that are not important to the interpretation of the question.
        m_garbageWords = new String[]
        {
            "is", "in", "has", "have", "of", "date", "the", "me", "a", "was", "does"
        };
    }

    // Check if a word is in the array of words of the question.
    private boolean collectionContains(String word, String[] wordCollection)
    {
        for (String toTest : wordCollection)
        {
            if (word.equalsIgnoreCase(toTest))
            {
                return true;
            }
        }

        return false;
    }

    // Add meaningful words to the meaning array.
    public void setMeaning()
    {
        String[] words = m_input.split(" ");

        for (String word : words)
        {
            word = word.replace("?", "");
            word = word.replace(".", "");
            word = word.replace("!", "");
            word = word.replace("+", "%2B");
            word = word.replace("/", "%2F");

            if (collectionContains(word, m_meanings))
            {
                m_meaning.add(word);
            }
            else if (!collectionContains(word, m_garbageWords))
            {
                m_name.add(word);
            }
        }

        m_input = String.join(", ", words);
    }

    public ArrayList<String> getMeaning()
    {
        return m_meaning;
    }

    public ArrayList<String> getname()
    {
        return m_name;
    }
}