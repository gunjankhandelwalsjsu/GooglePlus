package com.example.rajeshkhandelwal.g;

/**
 * Created by rajeshkhandelwal on 3/11/15.
 */
public class View {

        private static final char type1 = '=';
        private static final char type2 = '+';
        private static final char type3 = '~';
        private static final char type4 = '-';
        private static final char type5 = ' ';

        private static char getSelectedChar(int type)
        {
            switch(type)
            {
                case 1: return type1;
                case 2: return type2;
                case 3: return type3;
                case 4: return type4;
                case 5: return type5;
                default: return ' ';
            }
        }

        public static void header(String name, int type)
        {
            char selectedChar = getSelectedChar(type);
            String pre = "\n", post = " ";

            for(int index = 0; index < 18; index++)
            {
                pre += selectedChar;
                post += selectedChar;
            }
            pre += " ";
            System.out.println(pre + name + post);
        }

        public static void note(String content, Boolean isError)
        {
            if(isError == false)
                System.out.println("NOTE: " + content);
            else
                System.err.println("ERROR: " + content);
        }

        public static void separator(int type)
        {
            char selectedChar = getSelectedChar(type);
            String output = "";
            for(int index = 0; index < 54; index++)
            {
                output += selectedChar;
            }
            System.out.println(output);
        }

        public static void printString(Object data, String type)
        {
            System.out.println(type + ": " + (data == null ? "NO DATA" : data));
        }

    }

