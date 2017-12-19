import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.Map;

import com.google.gson.*;


// Will make all data holing JsonObjects class variables
public class StarWarsAPI3 {
    // People: 87 done
    // Films: 7 done
    // Starships: 37 done
    // Vehicles: 39 done
    // Species: 37 done
    // Planets: 61 done

    // these will store names mapped to JsonObjects
    private JsonObject filmObject, peopleObject, starShipsObject, vehicleObject,
        speciesObject, planetObject;
    // these will store urls mapped to JsonObjects
    private JsonObject urls;
    private String baseURL = "https://swapi.co/api/";

    // signals that the JsonObjects are loaded or to be loaded
    private boolean toLoad;
    private boolean loaded;
    public StarWarsAPI3() {
        filmObject = new JsonObject();
        peopleObject = new JsonObject();
        starShipsObject = new JsonObject();
        vehicleObject = new JsonObject();
        speciesObject = new JsonObject();
        planetObject = new JsonObject();
        urls = new JsonObject();
        toLoad = false;
        loaded = false;
    }

    public static void main(String[] args) {
        StarWarsAPI3 swapi = new StarWarsAPI3();
        System.out.println("Welcome to the Star Wars API.");
        System.out.println("As you make more diverse requests, additional" +
            " data is stored. If extensive information is required, recommend" +
            " selecting the 'Load All' option");
        try {
            swapi.enter();
        } catch(IOException e) {
            System.out.println("Exception occured.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
    * Method which takes in initial input about what user wants. Contains prompts
    * and calls right functions based on what the user wants information on.
    */
    private void enter() throws IOException {
        for (;;) {
            System.out.println("\n\nWhat would you like information about?");
            System.out.println("1. People.\n2. Planets.\n3. Films.\n4. Species.");
            System.out.println("5. Vehicles.\n6. StarShips\n7. Load All\n8. Exit");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Choice: ");
            int choice;
            try {
                choice = Integer.parseInt(br.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter the number of your selection.");
                continue;
            }
            JsonObject info;
            switch (choice) {
            case 1:
                // System.out.println(conversionToPrettyString(getDataAsJson("people")));
                if (loaded) {
                    peopleObjectExtraction(null, peopleObject);
                    continue;
                }
                info = getDataAsJson("people/?format=json");
                peopleObjectExtraction(info, peopleObject);
                break;
            case 2:
                // System.out.println(conversionToPrettyString(getDataAsJson("planets")));
                if (loaded) {
                    planetObjectExtraction(null, planetObject);
                    continue;
                }
                info = getDataAsJson("planets/?format=json");
                planetObjectExtraction(info, planetObject);
                break;
            case 3:
                // System.out.println(conversionToPrettyString(getDataAsJson("films")));
                 if (loaded) {
                    filmsObjectExtraction(null, filmObject);
                    continue;
                }
                info = getDataAsJson("films/?format=json");
                filmsObjectExtraction(info, filmObject);
                break;
            case 4:
                // System.out.println(conversionToPrettyString(getDataAsJson("species")));
                if (loaded) {
                    speciesObjectExtraction(null, speciesObject);
                    continue;
                }
                info = getDataAsJson("species/?format=json");
                speciesObjectExtraction(info, speciesObject);
                break;
            case 5:
                // System.out.println(conversionToPrettyString(getDataAsJson("vehicles")));
                if (loaded) {
                    vehicleObjectExtraction(null, vehicleObject);
                    continue;
                }
                info = getDataAsJson("vehicles/?format=json");
                vehicleObjectExtraction(info, vehicleObject);
                break;
            case 6:
                // System.out.println(conversionToPrettyString(getDataAsJson("starships")));
                if (loaded) {
                    starShipsObjectExtraction(null, starShipsObject);
                    continue;
                }
                info = getDataAsJson("starships/?format=json");
                starShipsObjectExtraction(info, starShipsObject);
                break;
            case 7:
                if (!loaded) {
                    System.out.println("This might take some time....");
                    toLoad = true;
                    System.out.println("Loading people information....");
                    JsonObject peopleInfo = getDataAsJson("people/?format=json");
                    peopleObjectExtraction(peopleInfo, peopleObject);
                    System.out.println("Loading planet information....");
                    JsonObject planetInfo = getDataAsJson("planets/?format=json");
                    planetObjectExtraction(planetInfo, planetObject);
                    System.out.println("Loading film information....");
                    JsonObject filmInfo = getDataAsJson("films/?format=json");
                    filmsObjectExtraction(filmInfo, filmObject);
                    System.out.println("Loading species information....");
                    JsonObject speciesInfo = getDataAsJson("species/?format=json");
                    speciesObjectExtraction(speciesInfo, speciesObject);
                    System.out.println("Loading vehicle information....");
                    JsonObject vehicleInfo = getDataAsJson("vehicles/?format=json");
                    vehicleObjectExtraction(vehicleInfo, vehicleObject);
                    System.out.println("Loading starships information....");
                    JsonObject starShipsInfo = getDataAsJson("starships/?format=json");
                    starShipsObjectExtraction(starShipsInfo, starShipsObject);
                    loaded = true;
                    toLoad = false;
                    postLoadAll();
                }
                break;
            case 8:
                System.out.println("Thanks. Hope you enjoyed this. Now poda.");
                System.exit(0);
            default:
                System.out.println("Incorrect option.");
            }
        }
    }

    private void postLoadAll() {
        write(peopleObject, "people.json");
        write(planetObject, "planets.json");
        write(filmObject, "films.json");
        write(speciesObject, "species.json");
        write(vehicleObject, "vehicle.json");
        write(starShipsObject, "starships.json");
        write(urls, "urls.json");
        System.out.println("Done writing to files");
    }

    private void write(JsonObject object, String fileName) {
        try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(conversionToPrettyString(object));
            writer.close();
        } catch (IOException e) {
           System.out.println("Error while writing to file.");
           System.out.println(e.getMessage());
        }
    }

    /**
    * Takes in the response for vehicles and loads passed in JsonObject with the names
    * of the vehicles as keys and the actual Json information as its corresponding
    * values
    */
    private void vehicleObjectExtraction(JsonObject vehicles, JsonObject vehicleObject) throws IOException {
        if (vehicles == null) {
            postLoadingVehicles(vehicleObject);
        }
        if (vehicles.get("count").getAsInt() == vehicleObject.size() && toLoad) {
            System.out.println("Information already available\n");
            return;
        }
        if (vehicles.get("count").getAsInt() == vehicleObject.size()) {
            // removes api unneccessay calls if object is already loaded
            postLoadingVehicles(vehicleObject);
            return;
        }
        System.out.println("Populated to size = " + vehicleObject.size());
        // contains URL of next page if any
        JsonElement nextURLElem = vehicles.get("next");
        String nextURL = "";
        JsonArray results = vehicles.get("results").getAsJsonArray();
        if (!nextURLElem.isJsonNull()) {
            nextURL = nextURLElem.getAsString();
            nextURL = nextURL.substring(baseURL.length(), nextURL.length());
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                vehicleObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            // recursively finds and populates all the data for planets into planetObject
            vehicleObjectExtraction(getDataAsJson(nextURL), vehicleObject);
        } else {
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                vehicleObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            System.out.println("Populated to size = " + vehicleObject.size());
            System.out.println("Vehicle information has been obtained.\n");
        }
        if (!toLoad) {
            postLoadingVehicles(vehicleObject);
        }
    }

    /**
    * Takes the loaded JsonObject, prints its keys and handles prompting
    * Passes the needed JsonObject to another method
    *
    * @param is the loaded JsonObject containing the Objects mapped to their names
    */
    private void postLoadingVehicles(JsonObject vehicleObject) throws IOException {
        System.out.println("\nWhat would you like to do now?");
        System.out.println("The different vehicles are: ");
        Set<Map.Entry<String, JsonElement>> entries = vehicleObject.entrySet();
        //will return members of your object
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            int c = 0;
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println((++c) + ". " + entry.getKey());
            }
            System.out.println("Which vehicle do you want to know more about?");
            System.out.println("Type return to return to selection screen. Exit to exit.");
            System.out.print("Enter name: ");
            String name = br.readLine();
            if (name.equalsIgnoreCase("return")) {
                enter();
            }
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Exiting....");
                System.exit(0);
            }
            if (vehicleObject.get(name) != null) {
                displayVehicleInfo(vehicleObject.get(name).getAsJsonObject(), br);
            } else {
                System.out.println("The vehicle does not exist.");
                postLoadingVehicles(vehicleObject);
            }
        }
    }

    /**
    * Takes the required JsonObject and prints keys within it.
    * Prompts user about which property they want to learn more about
    *
    * @param vehicle is the required JsonObject containing the properties
    *   mapped to their names
    * @param br is a BufferedReader instance used to accept input from the console
    */
    private void displayVehicleInfo(JsonObject vehicle, BufferedReader br) throws IOException {
        System.out.println("\nCurrent vehicle is: " + vehicle.get("name")
            .getAsString());
        System.out.println("The properties in the object are: ");
        Set<Map.Entry<String, JsonElement>> entries = vehicle.entrySet();
        int c = 0;
        for (Map.Entry<String, JsonElement> entry: entries) {
            System.out.println("- " + entry.getKey());
        }
        System.out.println("Which property would you like to know more about?");
        System.out.print("Property: ");
        String property = br.readLine();
        JsonElement elem = vehicle.get(property);
        if (elem != null) {
            if (elem.isJsonPrimitive()) {
                // System.out.println("\nThe property has value: \n" + elem.getAsString() + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                if (urls.has(elem.getAsString()) && !property.equalsIgnoreCase("url")) {
                    JsonObject temp = urls.get(elem.getAsString()).getAsJsonObject();
                    if (temp.get("name") == null) {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("title").getAsString());
                    } else {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("name").getAsString());
                    }
                } else {
                    System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                }
            } else {
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                c = 0;
                if (elem.isJsonArray()) {
                    JsonArray properties = elem.getAsJsonArray();
                    for (JsonElement e: properties) {
                        // System.out.println((++c) + ". " + filmURLs
                        //     .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        if (!urls.has(e.getAsString())) {
                            System.out.println((++c) + ". " + e.getAsString());
                            continue;
                        }
                        JsonObject temp = urls.get(e.getAsString()).getAsJsonObject();
                        if (temp.get("name") == null) {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("title").getAsString());
                        } else {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        }
                    }
                } else {
                    throw new IOException("Element is not a JsonArray");
                }
            }
        }
        System.out.println("Continue with this vehicle?");
        System.out.print("Y = Yes, N = No: ");
        String choice = br.readLine();
        if (choice.equalsIgnoreCase("y")) {
            displayVehicleInfo(vehicle, br);
        }
    }

    /**
    * Takes in the response for planets and loads passed in JsonObject with the names
    * of the planets as keys and the actual Json information as its corresponding
    * values
    */
    private void planetObjectExtraction(JsonObject planets, JsonObject planetObject) throws IOException {
        if (planets == null) {
            postLoadingPlanets(planetObject);
        }
        if (planets.get("count").getAsInt() == planetObject.size() && toLoad) {
            System.out.println("Information already available\n");
            return;
        }
        if (planets.get("count").getAsInt() == planetObject.size()) {
            // removes api unneccessay calls if object is already loaded
            postLoadingPlanets(planetObject);
            return;
        }
        System.out.println("Populated to size = " + planetObject.size());
        // contains URL of next page if any
        JsonElement nextURLElem = planets.get("next");
        String nextURL = "";
        JsonArray results = planets.get("results").getAsJsonArray();
        if (!nextURLElem.isJsonNull()) {
            nextURL = nextURLElem.getAsString();
            nextURL = nextURL.substring(baseURL.length(), nextURL.length());
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                planetObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            // recursively finds and populates all the data for planets into planetObject
            planetObjectExtraction(getDataAsJson(nextURL), planetObject);
        } else {
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                planetObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            System.out.println("Populated to size = " + planetObject.size());
            System.out.println("Planet information has been obtained.\n");
        }
        if (!toLoad) {
            postLoadingPlanets(planetObject);
        }
    }

    /**
    * Takes the loaded JsonObject, prints its keys and handles prompting
    * Passes the needed JsonObject to another method
    *
    * @param is the loaded JsonObject containing the Objects mapped to their names
    */
    private void postLoadingPlanets(JsonObject planetObject) throws IOException {
        System.out.println("\nWhat would you like to do now?");
        System.out.println("The different planets are: ");
        Set<Map.Entry<String, JsonElement>> entries = planetObject.entrySet();
        //will return members of your object
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            int c = 0;
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println((++c) + ". " + entry.getKey());
            }
            System.out.println("Which planet do you want to know more about?");
            System.out.println("Type return to return to selection screen. Exit to exit");
            System.out.print("Enter name: ");
            String name = br.readLine();
            if (name.equalsIgnoreCase("return")) {
                enter();
            }
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Exiting.");
                System.exit(0);
            }
            if (planetObject.get(name) == null) {
                System.out.println("The planet does not exist. Retrying.");
                postLoadingPlanets(planetObject);
            } else {
                displayPlanetInfo(planetObject.get(name).getAsJsonObject(), br);
            }
        }
    }

    /**
    * Takes the required JsonObject and prints keys within it.
    * Prompts user about which property they want to learn more about
    *
    * @param planet is the required JsonObject containing the properties
    *   mapped to their names
    * @param br is a BufferedReader instance used to accept input from the console
    */
    private void displayPlanetInfo(JsonObject planet, BufferedReader br) throws IOException {
        System.out.println("\nCurrent planet is: " + planet.get("name")
            .getAsString());
        System.out.println("The properties in the object are: ");
        Set<Map.Entry<String, JsonElement>> entries = planet.entrySet();
        int c = 0;
        for (Map.Entry<String, JsonElement> entry: entries) {
            System.out.println("- " + entry.getKey());
        }
        System.out.println("Which property would you like to know more about?");
        System.out.print("Property: ");
        String property = br.readLine();
        JsonElement elem = planet.get(property);
        if (elem != null) {
            if (elem.isJsonPrimitive()) {
                // System.out.println("\nThe property has value: \n" + elem.getAsString() + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                if (urls.has(elem.getAsString()) && !property.equalsIgnoreCase("url")) {
                    JsonObject temp = urls.get(elem.getAsString()).getAsJsonObject();
                    if (temp.get("name") == null) {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("title").getAsString());
                    } else {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("name").getAsString());
                    }
                } else {
                    System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                }
            } else {
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                c = 0;
                if (elem.isJsonArray()) {
                    JsonArray properties = elem.getAsJsonArray();
                    for (JsonElement e: properties) {
                        // System.out.println((++c) + ". " + filmURLs
                        //     .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        if (!urls.has(e.getAsString())) {
                            System.out.println((++c) + ". " + e.getAsString());
                            continue;
                        }
                        JsonObject temp = urls.get(e.getAsString()).getAsJsonObject();
                        if (temp.get("name") == null) {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("title").getAsString());
                        } else {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        }
                    }
                } else {
                    throw new IOException("Element is not a JsonArray");
                }
            }
        }
        System.out.println("Continue with this planet?");
        System.out.print("Y = Yes, N = No: ");
        String choice = br.readLine();
        if (choice.equalsIgnoreCase("y")) {
            displayPlanetInfo(planet, br);
        }
    }

    /**
    * Takes in the response for people and loads passed in JsonObject with the names
    * of the people as keys and the actual Json information as its corresponding
    * values
    */
    private void peopleObjectExtraction(JsonObject people, JsonObject peopleObject) throws IOException {
        if (people == null) {
            postLoadingPeople(peopleObject);
        }
        if (people.get("count").getAsInt() == peopleObject.size() && toLoad) {
            System.out.println("Information already available\n");
            return;
        }
        if (people.get("count").getAsInt() == peopleObject.size()) {
            // removes api unneccessay calls if object is already loaded
            postLoadingPeople(peopleObject);
            return;
        }
        System.out.println("Populated to size = " + peopleObject.size());
        // contains URL of next page if any
        JsonElement nextURLElem = people.get("next");
        String nextURL = "";
        JsonArray results = people.get("results").getAsJsonArray();
        if (!nextURLElem.isJsonNull()) {
            nextURL = nextURLElem.getAsString();
            nextURL = nextURL.substring(baseURL.length(), nextURL.length());
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                peopleObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            // recursively finds and populates all the data for people into peopleObject
            peopleObjectExtraction(getDataAsJson(nextURL), peopleObject);
        } else {
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                peopleObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            System.out.println("Populated to size = " + peopleObject.size());
            System.out.println("People information has been obtained.\n");
        }
        if (!toLoad) {
            postLoadingPeople(peopleObject);
        }
    }

    /**
    * Takes the loaded JsonObject, prints its keys and handles prompting
    * Passes the needed JsonObject to another method
    *
    * @param is the loaded JsonObject containing the Objects mapped to their names
    */
    private void postLoadingPeople(JsonObject peopleObject) throws IOException {
        System.out.println("\nWhat would you like to do now?");
        System.out.println("The different people are: ");
        Set<Map.Entry<String, JsonElement>> entries = peopleObject.entrySet();
        //will return members of your object
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            int c = 0;
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println((++c) + ". " + entry.getKey());
            }
            System.out.println("Which person do you want to know more about?");
            System.out.println("Type return to return to selection screen. Exit to exit");
            System.out.print("Enter name: ");
            String name = br.readLine();
            if (name.equalsIgnoreCase("return")) {
                enter();
            }
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                System.exit(0);
            }
            if (peopleObject.get(name) == null) {
                System.out.println("The person does not exist. Retrying...");
                postLoadingPeople(peopleObject);
            } else {
                displayPeopleInfo(peopleObject.get(name).getAsJsonObject(), br);
            }
        }
    }

    /**
    * Takes the required JsonObject and prints keys within it.
    * Prompts user about which property they want to learn more about
    *
    * @param person is the loaded JsonObject containing the Objects
    *   mapped to their names
    * @param br is a BufferedReader instance used to accept input from the console
    */
    private void displayPeopleInfo(JsonObject person, BufferedReader br) throws IOException {
        System.out.println("\nCurrent person is: " + person.get("name")
            .getAsString());
        System.out.println("The properties in the object are: ");
        Set<Map.Entry<String, JsonElement>> entries = person.entrySet();
        int c = 0;
        for (Map.Entry<String, JsonElement> entry: entries) {
            System.out.println("- " + entry.getKey());
        }
        System.out.println("Which property would you like to know more about?");
        System.out.print("Property: ");
        String property = br.readLine();
        JsonElement elem = person.get(property);
        if (elem != null) {
            if (elem.isJsonPrimitive()) {
                // System.out.println("\nThe property has value: \n" + elem.getAsString() + "\n\n");
                if (urls.has(elem.getAsString()) && !property.equalsIgnoreCase("url")) {
                    JsonObject temp = urls.get(elem.getAsString()).getAsJsonObject();
                    if (temp.get("name") == null) {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("title").getAsString());
                    } else {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("name").getAsString());
                    }
                } else {
                    System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                }
            } else {
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                c = 0;
                if (elem.isJsonArray()) {
                    JsonArray properties = elem.getAsJsonArray();
                    for (JsonElement e: properties) {
                        // System.out.println((++c) + ". " + filmURLs
                        //     .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        if (!urls.has(e.getAsString())) {
                            System.out.println((++c) + ". " + e.getAsString());
                            continue;
                        }
                        JsonObject temp = urls.get(e.getAsString()).getAsJsonObject();
                        if (temp.get("name") == null) {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("title").getAsString());
                        } else {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        }
                    }
                } else {
                    throw new IOException("Element is not a JsonArray");
                }
            }
        }
        System.out.println("Continue with this person?");
        System.out.print("Y = Yes, N = No: ");
        String choice = br.readLine();
        if (choice.equalsIgnoreCase("y")) {
            displayPeopleInfo(person, br);
        }
    }

    /**
    * Takes in the response for starShips and loads passed in JsonObject with the names
    * of the starShips as keys and the actual Json information as its corresponding
    * values
    */
    private void starShipsObjectExtraction(JsonObject starShips, JsonObject starShipsObject) throws IOException {
        if (starShips == null) {
            postLoadingStarShips(starShipsObject);
        }
        if (starShips.get("count").getAsInt() == starShipsObject.size() && toLoad) {
            System.out.println("Information already available\n");
            return;
        }
        if (starShips.get("count").getAsInt() == starShipsObject.size()) {
            // removes api unneccessay calls if object is already loaded
            postLoadingStarShips(starShipsObject);
            return;
        }
        System.out.println("Populated to size = " + starShipsObject.size());
        // contains URL of next page if any
        JsonElement nextURLElem = starShips.get("next");
        String nextURL = "";
        JsonArray results = starShips.get("results").getAsJsonArray();
        if (!nextURLElem.isJsonNull()) {
            nextURL = nextURLElem.getAsString();
            nextURL = nextURL.substring(baseURL.length(), nextURL.length());
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                starShipsObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            // recursively finds and populates all the data for starships into starShipsObject
            starShipsObjectExtraction(getDataAsJson(nextURL), starShipsObject);
        } else {
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                starShipsObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            System.out.println("Populated to size = " + starShipsObject.size());
            System.out.println("Star Ship information has been obtained.\n");
        }
        if (!toLoad) {
            postLoadingStarShips(starShipsObject);
        }
    }

    /**
    * Takes the loaded JsonObject, prints its keys and handles prompting
    * Passes the needed JsonObject to another method
    *
    * @param is the loaded JsonObject containing the Objects mapped to their names
    */
    private void postLoadingStarShips(JsonObject starShipsObject) throws IOException {
        System.out.println("\nWhat would you like to do now?");
        System.out.println("The different starships are: ");
        Set<Map.Entry<String, JsonElement>> entries = starShipsObject.entrySet();
        //will return members of your object
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            int c = 0;
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println((++c) + ". " + entry.getKey());
            }
            System.out.println("Which STARSHIP do you want to know more about?");
            System.out.println("Type return to return to selection screen. Exit to exit.");
            System.out.print("Enter name: ");
            String name = br.readLine();
            if (name.equalsIgnoreCase("return")) {
                enter();
            }
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Exiting..");
                System.exit(0);
            }
            if (starShipsObject.get(name) == null) {
                System.out.println("The starship does not exist. Retrying.");
                postLoadingStarShips(starShipsObject);
            } else {
                displayStarShipsInfo(starShipsObject.get(name).getAsJsonObject(), br);
            }
        }
    }

    /**
    * Takes the required JsonObject and prints keys within it.
    * Prompts user about which property they want to learn more about
    *
    * @param starShip is the loaded JsonObject containing the Objects
    *   mapped to their names
    * @param br is a BufferedReader instance used to accept input from the console
    */
    private void displayStarShipsInfo(JsonObject starShip, BufferedReader br) throws IOException {
        System.out.println("\nCurrent starship is: " + starShip.get("name")
            .getAsString());
        System.out.println("The properties in the object are: ");
        Set<Map.Entry<String, JsonElement>> entries = starShip.entrySet();
        int c = 0;
        for (Map.Entry<String, JsonElement> entry: entries) {
            System.out.println("- " + entry.getKey());
        }
        System.out.println("Which property would you like to know more about?");
        System.out.print("Property: ");
        String property = br.readLine();
        JsonElement elem = starShip.get(property);
        if (elem != null) {
            if (elem.isJsonPrimitive()) {
                // System.out.println("\nThe property has value: \n" + elem.getAsString() + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                if (urls.has(elem.getAsString()) && !property.equalsIgnoreCase("url")) {
                    JsonObject temp = urls.get(elem.getAsString()).getAsJsonObject();
                    if (temp.get("name") == null) {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("title").getAsString());
                    } else {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("name").getAsString());
                    }
                } else {
                    System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                }
            } else {
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                c = 0;
                if (elem.isJsonArray()) {
                    JsonArray properties = elem.getAsJsonArray();
                    for (JsonElement e: properties) {
                        if (!urls.has(e.getAsString())) {
                            System.out.println((++c) + ". " + e.getAsString());
                            continue;
                        }
                        // System.out.println((++c) + ". " + filmURLs
                        //     .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        JsonObject temp = urls.get(e.getAsString()).getAsJsonObject();
                        if (temp.get("name") == null) {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("title").getAsString());
                        } else {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        }
                    }
                } else {
                    throw new IOException("Element is not a JsonArray");
                }
            }
        }
        System.out.println("Continue with this star ship?");
        System.out.print("Y = Yes, N = No: ");
        String choice = br.readLine();
        if (choice.equalsIgnoreCase("y")) {
            displayStarShipsInfo(starShip, br);
        }
    }


    /**
    * Takes in the response for species and loads passed in JsonObject with the names
    * of the species as keys and the actual Json information as its corresponding
    * values
    */
    private void speciesObjectExtraction(JsonObject species, JsonObject speciesObject) throws IOException {
        if (species == null) {
            postLoadingSpecies(speciesObject);
        }
        if (species.get("count").getAsInt() == speciesObject.size() && toLoad) {
            System.out.println("Information already available\n");
            return;
        }
        if (species.get("count").getAsInt() == speciesObject.size()) {
            // removes api unneccessay calls if object is already loaded
            postLoadingSpecies(speciesObject);
            return;
        }
        System.out.println("Populated to size = " + speciesObject.size());
        // contains URL of next page if any
        JsonElement nextURLElem = species.get("next");
        String nextURL = "";
        JsonArray results = species.get("results").getAsJsonArray();
        if (!nextURLElem.isJsonNull()) {
            nextURL = nextURLElem.getAsString();
            nextURL = nextURL.substring(baseURL.length(), nextURL.length());
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                speciesObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            // recursively finds and populates all the data for species into speciesObject
            speciesObjectExtraction(getDataAsJson(nextURL), speciesObject);
        } else {
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("name").getAsString();
                speciesObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            System.out.println("Populated to size = " + speciesObject.size());
            System.out.println("Species information has been obtained.\n");
        }
        if (!toLoad) {
            postLoadingSpecies(speciesObject);
        }
    }

    /**
    * Takes the loaded JsonObject, prints its keys and handles prompting
    * Passes the needed JsonObject to another method
    *
    * @param is the loaded JsonObject containing the Objects mapped to their names
    */
    private void postLoadingSpecies(JsonObject speciesObject) throws IOException {
        System.out.println("\nWhat would you like to do now?");
        System.out.println("The different species are: ");
        Set<Map.Entry<String, JsonElement>> entries = speciesObject.entrySet();
        //will return members of your object
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            int c = 0;
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println((++c) + ". " + entry.getKey());
            }
            System.out.println("Which species do you want to know more about?");
            System.out.println("Type return to return to selection screen");
            System.out.print("Enter name: ");
            String name = br.readLine();
            if (name.equalsIgnoreCase("return")) {
                enter();
            }
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                System.exit(0);
            }
            if (speciesObject.get(name) == null) {
                System.out.println("The species does not exist. Retrying.");
                postLoadingSpecies(speciesObject);
            } else {
                displaySpeciesInfo(speciesObject.get(name).getAsJsonObject(), br);
            }
        }
    }

    /**
    * Takes the required JsonObject and prints keys within it.
    * Prompts user about which property they want to learn more about
    *
    * @param speciesInstance is the loaded JsonObject containing the Objects
    *   mapped to their names
    * @param br is a BufferedReader instance used to accept input from the console
    */
    private void displaySpeciesInfo(JsonObject speciesInstance, BufferedReader br) throws IOException {
        System.out.println("\nCurrent species is: " + speciesInstance.get("name")
            .getAsString());
        System.out.println("The properties in the object are: ");
        Set<Map.Entry<String, JsonElement>> entries = speciesInstance.entrySet();
        int c = 0;
        for (Map.Entry<String, JsonElement> entry: entries) {
            System.out.println("- " + entry.getKey());
        }
        System.out.println("Which property would you like to know more about?");
        System.out.print("Property: ");
        String property = br.readLine();
        JsonElement elem = speciesInstance.get(property);
        if (elem != null) {
            if (elem.isJsonPrimitive()) {
                // System.out.println("\nThe property has value: \n" + elem.getAsString() + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                if (urls.has(elem.getAsString()) && !property.equalsIgnoreCase("url")) {
                    JsonObject temp = urls.get(elem.getAsString()).getAsJsonObject();
                    if (temp.get("name") == null) {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("title").getAsString());
                    } else {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("name").getAsString());
                    }
                } else {
                    System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                }
            } else {
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                c = 0;
                if (elem.isJsonArray()) {
                    JsonArray properties = elem.getAsJsonArray();
                    for (JsonElement e: properties) {
                        // System.out.println((++c) + ". " + filmURLs
                        //     .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        if (!urls.has(e.getAsString())) {
                            System.out.println((++c) + ". " + e.getAsString());
                            continue;
                        }
                        JsonObject temp = urls.get(e.getAsString()).getAsJsonObject();
                        if (temp.get("name") == null) {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("title").getAsString());
                        } else {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        }
                    }
                } else {
                    throw new IOException("Element is not a JsonArray");
                }
            }
        }
        System.out.println("Continue with this species?");
        System.out.print("Y = Yes, N = No: ");
        String choice = br.readLine();
        if (choice.equalsIgnoreCase("y")) {
            displaySpeciesInfo(speciesInstance, br);
        }
    }

    /**
    * Sends get requests to the swapi server by appending the query to the base URL
    * then sets requested format to json format
    *
    * @param query the string to be appended to the end of the URL
    * @return the data received by the server after sending the GET request
    */
    private JsonObject getDataAsJson(String query) throws IOException {
        // System.out.println("\nEstablishing connection....");
        // 'this' keyword was added to avoid changing the value of the instance variable
        String baseURL = this.baseURL + query;
        URL url = new URL(baseURL);
        URLConnection con = url.openConnection();
        // taken of Google Chrome Cloud Console
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac "
            + "OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return new JsonParser().parse(response.toString()).getAsJsonObject();
    }

    /**
    * Pretty prints the passed in json string.
    * Used for debugging purposes
    *
    * @param json is the json to be pretty printed
    */
    private String conversionToPrettyString(JsonObject json) {
        if (json == null) {
            return "";
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = new JsonParser().parse(json.toString());
        // System.out.println(gson.toJson(je));
        return gson.toJson(je).toString();
    }

    /**
    * Takes in the response for films and loads a new JsonObject with the titles
    * of the films as keys and the actual Json information as its corresponding
    * values
    */
    private void filmsObjectExtraction(JsonObject films, JsonObject filmObject) throws IOException {
        if (films == null) {
            postLoadingFilms(filmObject);
        }
        if (films.get("count").getAsInt() == filmObject.size() && toLoad) {
            System.out.println("Information already available\n");
            return;
        }
        if (films.get("count").getAsInt() == filmObject.size()) {
            // removes api unneccessay calls if object is already loaded
            postLoadingFilms(filmObject);
            return;
        }
        System.out.println("Populated to size = " + filmObject.size());
        // contains URL of next page if any
        JsonElement nextURLElem = films.get("next");
        String nextURL = "";
        JsonArray results = films.get("results").getAsJsonArray();
        if (!nextURLElem.isJsonNull()) {
            nextURL = nextURLElem.getAsString();
            nextURL = nextURL.substring(baseURL.length(), nextURL.length());
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String title = curr.get("title").getAsString();
                filmObject.add(title, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            // recursively finds and populates all the data for planets into planetObject
            filmsObjectExtraction(getDataAsJson(nextURL), filmObject);
        } else {
            for (JsonElement e : results) {
                JsonObject curr = e.getAsJsonObject();
                String name = curr.get("title").getAsString();
                filmObject.add(name, curr);
                urls.add(curr.get("url").getAsString(), curr);
            }
            System.out.println("Populated to size = " + filmObject.size());
            System.out.println("Film information has been obtained.\n");
        }
        if (!toLoad) {
            postLoadingFilms(filmObject);
        }
    }

    /**
    * Takes the loaded JsonObject, prints its keys and handles prompting
    * Passes the needed JsonObject to another method
    *
    * @param is the loaded JsonObject containing the Objects mapped to their names
    */
    private void postLoadingFilms(JsonObject filmObject) throws IOException {
        System.out.println("\nWhat would you like to do now?");
        System.out.println("The different movies are: ");
        Set<Map.Entry<String, JsonElement>> entries = filmObject.entrySet();
        //will return members of your object
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            int c = 0;
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println((++c) + ". " + entry.getKey());
            }
            System.out.println("Which movie do you want to know more about?");
            System.out.println("Type return to return to selection screen");
            System.out.print("Enter name: ");
            String name = br.readLine();
            if (name.equalsIgnoreCase("return")) {
                enter();
            }
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Exiting.");
                System.exit(0);
            }
            if (filmObject.get(name) == null) {
                System.out.println("The movie does not exist. Retrying...");
                postLoadingFilms(filmObject);
            } else {
                displayFilmInfo(filmObject.get(name).getAsJsonObject(), br);
            }
        }
    }

    /**
    * Takes the required JsonObject and prints keys within it.
    * Prompts user about which property they want to learn more about
    *
    * @param filmInstance is the loaded JsonObject containing the Objects
    *   mapped to their names
    * @param br is a BufferedReader instance used to accept input from the console
    */
    private void displayFilmInfo(JsonObject filmInstance, BufferedReader br) throws IOException {
        System.out.println("\nCurrent movie is: " + filmInstance.get("title")
            .getAsString());
        System.out.println("The properties in the object are: ");
        Set<Map.Entry<String, JsonElement>> entries = filmInstance.entrySet();
        int c = 0;
        for (Map.Entry<String, JsonElement> entry: entries) {
            System.out.println("- " + entry.getKey());
        }
        System.out.println("Which property would you like to know more about?");
        System.out.print("Property: ");
        String property = br.readLine();
        JsonElement elem = filmInstance.get(property);
        if (elem != null) {
            if (elem.isJsonPrimitive()) {
                // System.out.println("\nThe property has value: \n" + elem.getAsString() + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                if (urls.has(elem.getAsString()) && !property.equalsIgnoreCase("url")) {
                    JsonObject temp = urls.get(elem.getAsString()).getAsJsonObject();
                    if (temp.get("name") == null) {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("title").getAsString());
                    } else {
                        System.out.println("- " + urls.get(elem.getAsString())
                            .getAsJsonObject().get("name").getAsString());
                    }
                } else {
                    System.out.println("\n" + property + ":\n" + elem.getAsString() + "\n\n");
                }
            } else {
                // System.out.println("\nThe property has value: " + elem + "\n\n");
                // System.out.println("\n" + property + ":\n" + elem + "\n\n");
                c = 0;
                if (elem.isJsonArray()) {
                    JsonArray properties = elem.getAsJsonArray();
                    for (JsonElement e: properties) {
                        // System.out.println((++c) + ". " + filmURLs
                        //     .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        if (!urls.has(e.getAsString())) {
                            System.out.println((++c) + ". " + e.getAsString());
                            continue;
                        }
                        JsonObject temp = urls.get(e.getAsString()).getAsJsonObject();
                        if (temp.get("name") == null) {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("title").getAsString());
                        } else {
                            System.out.println((++c) + ". " + urls
                            .get(e.getAsString()).getAsJsonObject().get("name").getAsString());
                        }
                    }
                } else {
                    throw new IOException("Element is not a JsonArray");
                }
            }
        }
        System.out.println("Continue with this movie?");
        System.out.print("Y = Yes, N = No: ");
        String choice = br.readLine();
        if (choice.equalsIgnoreCase("y")) {
            displayFilmInfo(filmInstance, br);
        }
    }
}