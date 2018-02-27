package ccc;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Ning on 2/23/18.
 */
public class DiscoverTest {
    static HashMap<String, List<String>> map = new HashMap<>();

    static {
        map.put("music", asList("songs", "albums", "artists", "playlists"));
        map.put("radio", asList("radio mixes", "artist radio", "public(stations)", "satellite radio", "local stations"));
        map.put("podcasts", asList("channels", "top podcasts"));
        map.put("news", asList("national", "international", "finance", "sports", "tech", "entertainment"));
        map.put("sports", asList("top sports talk"));
    }

    public static void main(String[] args) {

        String version = "v1";
        String product = "JPIVI";

        List<String> applicableProviders = getApplicableProviders(version, product);

        String provider = null; // provider can be null, can contains invalid provider
        List<String> finalProvider = validProvider(applicableProviders, provider);

        List<String> applicableGroupedTypes = getApplicableGroupedTypes(version, product);

        String type = null; // type can be null, can contains groupedType or mediaType.
        List<String> finalMediaType = validMediaType(applicableGroupedTypes, type);


        Document pipeline = new Document().append("provider", finalProvider)
                .append("mediaType", finalMediaType);

        System.out.println(pipeline.toJson().toString());

        Document result = getDiscover(pipeline);

        Document finalResult = parse(result, version, product);

        System.out.println("\nfinal results after formatting");
    }

    /**
     *
     * @param pipeline
     * @return
     *
     * 1. for different mediaTypes, each provider has different API
     * 2. * based on provider list, go through each provider to get available list of results
     *          * this will need group later for mixing a mediaType results from different providers.
     * 3. * based on mediaType, go through each mediaType, then go to available provider
     *
     * Aschronous call each provider
     */
    public static Document getDiscover(Document pipeline) {

        //String[] staticProviders = new String[]{"amazon", "deezer", "spotify", "npr", "tunein"};

        List<String> providers = (List<String>)pipeline.get("provider");

        String providerString = Arrays.deepToString(providers.toArray());

        System.out.println(providerString);

        List<String> mediaTypes = (List<String>)pipeline.get("mediaType");

        if (providerString.contains("amazon")) {
            System.out.println("\nasynchronously call amazon discover APIs");
            getDiscoverFromAmazon(mediaTypes);
        }

        if (providerString.contains("deezer")) {
            System.out.println("\nasynchronously call deezer discover APIs");
            getDiscoverFromDeezer(mediaTypes);
        }

        if (providerString.contains("spotify")) {
            System.out.println("\nasynchronously call spotify discover APIs");
            getDiscoverFromSpotify(mediaTypes);
        }

        if (providerString.contains("tunein")) {
            System.out.println("\nasynchronously call tunein discover APIs");
            getDiscoverFromTunein(mediaTypes);
        }

        if (providerString.contains("npr")) {
            System.out.println("\nasynchronously call npr discover APIs");
            getDiscoverFromNpr(mediaTypes);
        }

        return new Document();
    }

    public static List<String> validProvider(List<String> applicableProviders, String provider) {
        if (provider == null || provider.isEmpty()) {
            return applicableProviders;
        }

        String[] strs = provider.split(",");

        List<String> providers = new ArrayList<>();

        for (String p : strs) {
            if (applicableProviders.contains(p)) {
                providers.add(p);
            }
        }

        providers = providers.size() == 0 ? applicableProviders : providers;

        return providers;
    }

    public static List<String> validMediaType(List<String> applicableGroupedTypes, String type) {

        final List<String> applicableMediaTypes = getDefaultMediaTypes(applicableGroupedTypes);
        List<String> mediaTypes = new ArrayList<>();

        if (type == null || type.isEmpty()) {

        }

        else {
            String[] strs = type.split(",");
            for (String s : strs) {
                if (applicableGroupedTypes.contains(s)) {
                    mediaTypes.addAll(getChildMediaTypes(s));
                }
            }

            for (String p : strs) {
                if (applicableMediaTypes.contains(p) && !mediaTypes.contains(p)) {
                    mediaTypes.add(p);
                }
            }
        }

        mediaTypes = mediaTypes.size() == 0 ? applicableMediaTypes : mediaTypes;

        return mediaTypes;
    }

    public static List<String> getDefaultMediaTypes(List<String> applicableGroupedTypes) {
        List<String> mediaTypes = new ArrayList<>();

        for (String s : applicableGroupedTypes) {
            mediaTypes.addAll(getChildMediaTypes(s));
        }

        return mediaTypes;
    }

    public static List<String> getChildMediaTypes(String groupedtype) {
        return map.get(groupedtype);
    }

    public static List<String> getApplicableProviders(String version, String product) {
        String str = version + "_" + product;

        switch (str) {
            case "v1_JPIVI":
                return getV1JPIVIProviders();
            default:
                return null;
        }
    }

    public static List<String> getApplicableGroupedTypes(String version, String product) {
        String str = version + "_" + product;

        switch (str) {
            case "v1_JPIVI":
                return getV1JPIVIGroupedTypes();
            default:
                return null;
        }
    }


    public static List<String> getV1JPIVIProviders() {
        return asList("amazon", "deezer", "spotify", "tunein", "npr");
    }

    public static List<String> getV1JPIVIGroupedTypes() {
        //return asList("music", "radio", "podcasts", "news");
        return asList("music", "radio", "podcasts", "news", "sports");
    }

    /**
     *
     * currently, amazon supports "popularTracks", "popularAlbums", "popularStations", "popularPlaylists"
     *
     * @param mediaTypes
     */
    public static void getDiscoverFromAmazon(List<String> mediaTypes) {
        String mediaTypeString = Arrays.deepToString(mediaTypes.toArray());

        if (mediaTypeString.contains("songs")) {
            System.out.println("get popularTracks from amazon");
        }
        if (mediaTypeString.contains("albums")) {
            System.out.println("get popularAlbums from amazon");
        }
        if (mediaTypeString.contains("public(stations)")) {
            System.out.println("get popularStations from amazon");
        }
        if (mediaTypeString.contains("playlists")) {
            System.out.println("get popularPlaylists from amazon");
        }
    }

    /**
     *
     * currently, deezer supports "trending", "albums", "artists", "playlists", "radios", "tracks"
     *
     * 1, since we define trending as songs, so how about tracks here
     * 2, how to handle radios
     *
     * @param mediaTypes
     */
    public static void getDiscoverFromDeezer(List<String> mediaTypes) {
        String mediaTypeString = Arrays.deepToString(mediaTypes.toArray());

        if (mediaTypeString.contains("songs")) {
            System.out.println("get trending from deezer");
        }
        if (mediaTypeString.contains("albums")) {
            System.out.println("get albums from deezer");
        }
        if (mediaTypeString.contains("artists")) {
            System.out.println("get artists from deezer");
        }
        if (mediaTypeString.contains("playlists")) {
            System.out.println("get playlists from deezer");
        }
        // how to reach the radios since radio is a parent level, or if one child node meets ||
        if (mediaTypeString.contains("radios")) {
            System.out.println("get radios from deezer");
        }
    }

    /**
     *
     * currently, spotify supports "new releases", "artists", "playlists"
     * the functions are not implemented in platform
     *
     * @param mediaTypes
     */
    public static void getDiscoverFromSpotify(List<String> mediaTypes) {
        String mediaTypeString = Arrays.deepToString(mediaTypes.toArray());

        if (mediaTypeString.contains("songs")) {
            System.out.println("get new releases from spotify");
        }
        if (mediaTypeString.contains("artists")) {
            System.out.println("get followed artists from spotify");
        }
        if (mediaTypeString.contains("playlists")) {
            System.out.println("get featured playlists from spotify");
        }
    }

    /**
     * for tunein:
     * rectypes.add("sports").add("podcasts").add("news").add("am").add("fm").add("talk").add("recommended");
     *
     * @param mediaTypes
     */
    public static void getDiscoverFromTunein(List<String> mediaTypes) {
        String mediaTypeString = Arrays.deepToString(mediaTypes.toArray());

        if (mediaTypeString.contains("top sports talk")) {
            System.out.println("get sports from tunein");
        }

    }

    /**
     * for npr:
     * rectypes.add("");
     *
     * @param mediaTypes
     */
    public static void getDiscoverFromNpr(List<String> mediaTypes) {
        String mediaTypeString = Arrays.deepToString(mediaTypes.toArray());
    }

    public static Document parse(Document result, String version, String product) {
        return new Document();
    }
}

