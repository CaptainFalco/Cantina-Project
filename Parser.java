/*
* Future additions:
*   Selector Chains - Create a list of JsonObjects containing the first selector in the chain. Then check that list for the second selector and so on. (If selector chains is what I think they are.) 
*   Catch to make sure the correct file type is input.
* 
* Path I used to test on my computer. 
* C:\Users\Joe\Desktop\Cantina Project\SystemViewController.json
*/
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;
import com.google.gson.*;

public class Parser {
    public static void main(String[] args) throws Exception {
        Scanner userIn = new Scanner(System.in);
        System.out.print("Enter the file path of the json file you wish to parse: ");
        String fileDir = userIn.nextLine();
        System.out.println();
        // Import the file and begin parsing it.
        JsonElement file = new JsonParser().parse(new FileReader(fileDir));
        JsonObject jo = new JsonObject();
        jo = file.getAsJsonObject();

        // Create a collection of entries, (only contains top view)
        Set<Entry<String, JsonElement>> entries = jo.entrySet();
        Set<Entry<String, JsonElement>> result = new HashSet<Entry<String, JsonElement>>();

        // Go though entries in each level of the json file and store it in a set.
        Iterator<Entry<String, JsonElement>> it = entries.iterator();
        result.addAll(extractList(it));
        // printSet(result);

        // Begin loop for user input.
        String find = "";
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.print("Enter a selector for json (do not include symbols) (enter 'quit' to end the program): ");
            find = userIn.nextLine();
            // Exit Condition
            if (find.equals("quit")) {
                running = false;
                System.out.print("Ending the program.");
            } else { 
                // full system View
                if (find.equals("System")) {
                    System.out.println(jo.toString());
                } else { // Get view based on selector
                    Iterator<Entry<String, JsonElement>> list = result.iterator();
                    while (list.hasNext()) {
                        Entry<String, JsonElement> ent = list.next();
                        if (ent.getValue().isJsonArray()) {
                            JsonArray arr = ent.getValue().getAsJsonArray();
                            for (int i = 0; i < arr.size(); i++) {
                                JsonObject j = arr.get(i).getAsJsonObject();
                                parseFind(j, find);
                            }
                        } else {
                            parseFind(ent.getValue().getAsJsonObject(), find);
                        }
                    }
                }
            }
        }
    }
    // Method containing attributes to search for selectors. 
    // Add more *if* statements for more atributes.
    public static void parseFind(JsonObject j, String find) {
        if (j.has("class")) {
            String s = j.get("class").toString();
            s = s.replace("\"", "");
            if (s.equalsIgnoreCase(find)) {
                System.out.println(j.toString());
                System.out.println();
            }
        }
        if (j.has("classNames")) {
            JsonArray jarr = j.get("classNames").getAsJsonArray();
            for (int k = 0; k < jarr.size(); k++) {
                String s = jarr.get(k).toString();
                s = s.replace("\"", "");
                if (s.equalsIgnoreCase(find)) {
                    System.out.println(j.toString());
                    System.out.println();
                    break;
                }
            }
        }
        if (j.has("identifier")) {
            String s = j.get("identifier").toString();
            s = s.replace("\"", "");
            if (s.equalsIgnoreCase(find)) {
                System.out.println(j.toString());
                System.out.println();
            }
        }
    }

    // Initial parsing of the json into view lists.
    public static Set<Entry<String, JsonElement>> extractList(Iterator<Entry<String, JsonElement>> iter) {
        Set<Entry<String, JsonElement>> res = new HashSet<Entry<String, JsonElement>>();
        while (iter.hasNext()) {
            Entry<String, JsonElement> ent = iter.next();
            if (!ent.getKey().equals("classNames")) {
                // Add subview lists to the resulting set.
                if (ent.getKey().equals("subviews")) {
                    res.add(ent);
                    JsonArray arr = ent.getValue().getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject j = arr.get(i).getAsJsonObject();
                        Set<Entry<String, JsonElement>> entrys = j.entrySet();
                        Iterator<Entry<String, JsonElement>> it2 = entrys.iterator();
                        res.addAll(extractList(it2));
                    }
                // Add contentView and control objects to the set. *My prompt also mentions the view 'input', but I did not see any instances of it so I have not added it yet.*
                } else if (ent.getKey().equals("contentView") || ent.getKey().equals("control")) {
                    res.add(ent);
                    JsonObject j = ent.getValue().getAsJsonObject();
                    Set<Entry<String, JsonElement>> entrys = j.entrySet();
                    Iterator<Entry<String, JsonElement>> it2 = entrys.iterator();
                    res.addAll(extractList(it2));
                }
            }
        }
        return res;
    }

    public static void printSet(Set<Entry<String, JsonElement>> set) {
        Iterator<Entry<String, JsonElement>> iter = set.iterator();
        while (iter.hasNext()) {
            Entry<String, JsonElement> entry = iter.next();
            System.out.println(entry.getKey() + " : " + entry.getValue());
            System.out.println();
        }
    }
}