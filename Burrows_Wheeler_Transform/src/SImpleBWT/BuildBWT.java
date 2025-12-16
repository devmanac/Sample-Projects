package SImpleBWT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.out;

public class BuildBWT {

    public static String[] createCyclicArray(String refString) {
        StringBuilder stringBuilder = new StringBuilder(refString);
        int incrementer = 0;
        int inputStringLength = refString.length();
        String[] cirSuffixArray = new String[inputStringLength];

        // Create Circular Suffix Array
        for (int i=inputStringLength-1;i>=0;i--) {
            stringBuilder.insert(i, '#');
            String stringCutter = new String(stringBuilder);
            String[] splittedString = stringCutter.split("#");
            String newString = splittedString[1] + splittedString[0];
            stringBuilder.deleteCharAt(i);
            cirSuffixArray[incrementer] = newString;
            incrementer++;
        }

        return cirSuffixArray;
    }

    public static List<Integer> getHitLocations(char[] firstColumn, char[] lastColumn, int[] firstRank, int[] lastRank, String searchString) {
        String reverseString = new StringBuilder(searchString).reverse().toString();
        char[] reverseChars = reverseString.toCharArray();
        int columnLength = firstColumn.length;
        List<Integer> rowIndicesSelected = new ArrayList<>();
        for(int i=0;i<columnLength;i++) {
            if (reverseChars[0] == firstColumn[i]) {
                rowIndicesSelected.add(i);
            }
        }

        if(searchString.length() == 1 || rowIndicesSelected.isEmpty()) {
            return rowIndicesSelected;
        }

        for(int i=1;i<reverseString.length();i++) {
            List<Integer> lastRanksSelected = new ArrayList<>();
            for(Integer rowIndex: rowIndicesSelected) {
                if (lastColumn[rowIndex] == reverseChars[i]) {
                    lastRanksSelected.add(lastRank[rowIndex]);
                }
            }
            if(lastRanksSelected.isEmpty()) {
                return new ArrayList<>();
            }

            rowIndicesSelected = new ArrayList<>();
            for(int j=0;j<columnLength;j++) {
                if (reverseChars[i] == firstColumn[j] && lastRanksSelected.contains(firstRank[j])) {
                    rowIndicesSelected.add(j);
                }
            }
        }

        return rowIndicesSelected;

    }

    public static char[] getFirstColumn(String[] cirSuffixArray) {
        int length = cirSuffixArray.length;
        char[] firstColumn = new char[length];
        for (int i = 0; i < length; i++) {
            String s = cirSuffixArray[i];
            firstColumn[i] = s.charAt(0);
        }

        return firstColumn;
    }

    public static int[] firstRank(String[] cirSuffix) {
        int stringLength = cirSuffix.length;
        int[] firstRank = new int[stringLength];
        firstRank[0] = 1;
        firstRank[1] = 1;
        int counter = 1;
        char charInQn = cirSuffix[1].charAt(0);
        for (int i = 2; i < stringLength; i++) {
            String s = cirSuffix[i];
            if (s.charAt(0) == charInQn) {
                counter++;
            } else {
                counter = 1;
            }
            firstRank[i] = counter;
            charInQn = s.charAt(0);
        }

        return firstRank;
    }

    public static char[] getLastColumn(String[] cirSuffixArray) {
        int length = cirSuffixArray.length;
        char[] lastColumn = new char[length];
        for (int i = 0; i < length; i++) {
            String s = cirSuffixArray[i];
            lastColumn[i] = s.charAt(length-1);
        }

        return lastColumn;
    }

    public static int[] textPos(String[] cirSuffix) {
        int stringLength = cirSuffix.length;
        int[] textPos = new int[stringLength];
        for (int i = 0; i < stringLength; i++) {
            String s = cirSuffix[i];
            String[] split = s.split("\\$");
            if (split.length != 1) {
                textPos[i] = stringLength - split[0].length();
            } else {
                textPos[i] = 1;
            }
        }

        return textPos;
    }

    public static int[] lastRank(String[] cirSuffix, int[] firstRank) {
        int stringLength = cirSuffix.length;
        int[] lastRank = new int[stringLength];
        String searchString = cirSuffix[0];
        String compareString;
        for (int i = 0; i < stringLength; i++) {
            String[] splitByDollar = cirSuffix[i].split("\\$");
            if(splitByDollar.length == 2) {
                searchString = "$" + splitByDollar[1];
            }
            for (int j = 0; j < stringLength; j++) {
                String[] splitAgain = cirSuffix[j].split("\\$");
                if(splitAgain.length == 2) {
                    compareString = "$" + splitAgain[1] + cirSuffix[j].charAt(0);
                } else {
                    compareString = "$" + cirSuffix[j].charAt(0);
                }
                if (searchString.equals(compareString)) {
                    lastRank[i] = firstRank[j];
                    break;
                }
            }
        }

        return lastRank;
    }

    public static void printSortedFoundPositions(List<Integer> hits, int[] firstTextPos, String searchString) {
        List<Integer> foundPositions = new ArrayList<>();
        for(Integer hit : hits) {
            foundPositions.add(firstTextPos[hit] - 1);
        }
        Collections.sort(foundPositions);
        out.println(searchString + ":  " + foundPositions);
    }
}
