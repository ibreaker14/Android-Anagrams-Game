/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private static ArrayList<String> wordList;
    private static HashSet<String> wordSet;
    private static HashMap<String,ArrayList<String>> lettersToWord; //key: sorted version of string, value: arraylist of anagram words
    private static HashMap<Integer,ArrayList<String>> sizeToWords;
    private int wordLength = DEFAULT_WORD_LENGTH;

    //TODO constructor: store words in appropriate data structures
    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        wordList = new ArrayList<String>(); //stores each word read from dictionary file
        wordSet = new HashSet<String>();
        sizeToWords = new HashMap<Integer,ArrayList<String>>();
        lettersToWord = new HashMap<String, ArrayList<String>>();
        String line;

        while((line = in.readLine()) != null) {
            String word = line.toLowerCase().trim();
            wordSet.add(word);
            wordList.add(word);

            if(!lettersToWord.containsKey(sortLetters(word))){
                lettersToWord.put(sortLetters(word),new ArrayList<String>());
            }
            lettersToWord.get(sortLetters(word)).add(word);

            if(lettersToWord.get(sortLetters(word)).size() >= MIN_NUM_ANAGRAMS) { //extension only insert words with enough anagrams for starter words
                if (!sizeToWords.containsKey(word.length())) {
                    sizeToWords.put(word.length(), new ArrayList<String>());
                }
                sizeToWords.get(word.length()).add(word);
                Log.v("starter word sizes: ",String.valueOf(sizeToWords.size()));
            }
        }
    }

    //TODO asserts given word is in dictionary and isn't formed by adding a letter to start or end of word
    public boolean isGoodWord(String word, String base) {
        if(wordSet.contains(word)){
            if(word.indexOf(base) < 0){
                return true;
            }
        }
        return false;
    }

    //TODO create list of all possible anagrams of a given word
    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>(lettersToWord.get(sortLetters(targetWord)));
        return result;
    }

    //TODO creates a list of all possible words that can be formed by adding one more letter
    public List<String> getAnagramsWithOneMoreLetter(String word, boolean twoLetterMode) {
        ArrayList<String> result = new ArrayList<String>();
        for(char c = 'a' ; c <= 'z'; c++){
            String oneMoreLetter = sortLetters(word+String.valueOf(c));
            if(lettersToWord.containsKey(oneMoreLetter)){
                result.addAll(lettersToWord.get(oneMoreLetter));
            }

            if(twoLetterMode){
                for(char d = 'a';d <='z'; d++) {
                    String twoMoreLetters = sortLetters(oneMoreLetter + String.valueOf(d));
                    Log.v("two letter mode", twoMoreLetters);
                    if (lettersToWord.containsKey(twoMoreLetters)) {
                        result.addAll(lettersToWord.get(twoMoreLetters));
                    }
                }
            }
        }
        return result;
    }

    //TODO randomly selects a word with at least a desired number of anagrams
    public String pickGoodStarterWord() {
        Random rand = new Random();
        int  randomIndex = 0;

        while(!sizeToWords.containsKey(wordLength)){
            wordLength++;
        }
        ArrayList<String> wordPool = sizeToWords.get(wordLength);

        //don't need this anymore. Program optimized to remove bad starter words
       /* for(int i = 0;i< wordPool.size() ;i++){
            if(lettersToWord.get(sortLetters(wordPool.get(i))).size() >= MIN_NUM_ANAGRAMS){
                break;
            }
            if(i == wordPool.size() -1 && lettersToWord.get(sortLetters(wordPool.get(i))).size() < MIN_NUM_ANAGRAMS){
                i = 0;
                wordLength++;
                wordPool = sizeToWords.get(wordLength);
            }
        }*/

        do {
            randomIndex = rand.nextInt(wordPool.size());
        }while(lettersToWord.get(sortLetters((String)(wordPool.get(randomIndex)))).size() <  MIN_NUM_ANAGRAMS);

        if(wordLength < MAX_WORD_LENGTH){
            wordLength++;
        }

        return (wordPool.get(randomIndex));
    }

    //sorts letters of a word and returns a string with all letters in alphabetical order
    public String sortLetters(String word) {
        char[] charArray = word.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }
}
