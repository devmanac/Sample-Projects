import java.io.*;
import java.util.Arrays;
import java.util.List;

import static SImpleBWT.BuildBWT.*;
import static java.lang.System.*;


/*
Please provide the REFERENCE File Path as FIRST Argument in function main
Please provide the QUERIES File Path as SECOND Argument in function main
 */
public class SimpleBWT_ThomasJohn {
    public static void main(String[] args) throws IOException {
        BufferedReader brRef;
        BufferedReader brQueries;
        String referenceString = "";
        String query = "";
        try {
            brRef = new BufferedReader(new FileReader(args[0]));
            brQueries = new BufferedReader(new FileReader(args[1]));
        } catch (Exception e) {
            throw new RuntimeException("PLEASE provide REFERENCE and QUERY file path as args 0 & 1");
        }

        String line;
        while ((line = brRef.readLine()) != null) {
            if (!line.startsWith(">")) {
                referenceString = line + "$";
                break;
            }
        }

        String[] cirSuffixArray = createCyclicArray(referenceString);

        // Sorting the array - Sorted List created
        Arrays.sort(cirSuffixArray);

        /* 1. Build the Index
              firstColumn, firstRank, firstTextPosition, lastColumn, lastRank
              computed and stored into arrays
         */
        char[] firstColumn = getFirstColumn(cirSuffixArray);
        int[] firstRank = firstRank(cirSuffixArray);
        int[] firstTextPos = textPos(cirSuffixArray);
        char[] lastColumn = getLastColumn(cirSuffixArray);
        int[] lastRank = lastRank(cirSuffixArray, firstRank);

        // 3. Setup I/O and run your program
        // Output tested successfully with reference file and query file
        out.println("Reference :");
        out.println(referenceString);
        out.println("Occurrences of queries :");
        while ((line = brQueries.readLine()) != null) {
            if (!line.startsWith(">")) {
                query = line;
                //2. Implement Search - Search was implemented in getHitLocations function
                List<Integer> hits = getHitLocations(firstColumn, lastColumn, firstRank, lastRank, query);
                printSortedFoundPositions(hits, firstTextPos, query);
            }
        }
    }
}