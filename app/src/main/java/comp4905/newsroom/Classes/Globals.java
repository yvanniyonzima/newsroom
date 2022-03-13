package comp4905.newsroom.Classes;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Globals {
    //this is the information of the user loggen into this device
    public static User deviceUser;

    //these are all the topics that the user an choose from and will be used to filter searches
    public static String[] topics = {"News", "Sport", "Tech", "World", "Finance", "Politics",
            "Business", "Economics", "Entertainment", "Beauty", "Gaming"};

    //map of languages available to filter searches
    public static Map<String, String> languagesMap = Stream.of(new Object[][] {
            {"Bengali", "bn"},  {"Dutch", "nl"},  {"English", "en"},  {"French", "fr"},
            {"German", "de"},  {"Hindi", "hi"},  {"Italian", "it"},  {"Polish", "pl"},
            {"Portuguese", "pt"},  {"Punjabi", "pa"}, {"Russian", "rn"}, {"Spanish", "es"},
            {"Tamil", "ta"}, {"Turkish", "tr"}, {"Ukrainian", "uk"}
            }).collect(Collectors.toMap(p -> (String)p[0], p -> (String)p[1]));

    //languages for filter
    public static String[] languages = languagesMap.keySet().toArray(new String[0]);

    //country codes for filter
//    public static String[] countryCodes = {
//            "af","al","dz","ad","ao","ai","aq","ag","ar","am","aw","au","at","az","bs","bh","bd","bb","by","be","bz","bm","bt",
//            "bo","ba","bw","br","bn","bg","bi","kh","cm","ca","td","cl","cn","co","cg","hr","cu","cy","cz","dk","dj","ec","eg",
//            "er","ee","et","fo","fj","fi","fr","ga","gm","ge","de","gh","gi","gr","gd","gp","gu","gt","gn","gy","ht","hn","hu",
//            "is","in","id","ir","iq","ie","il","it","jm","jp","jo","kz","ke","kr","kw","kg","lk","la","lv","lb","lr","ly","li",
//            "lt","lu","mk","mg","mw","my","mv","ml","mt","mr","mu","yt","mx","fm","md","mc","mn","me","ms","ma","mz","mm","na",
//            "nr","np","nl","ni","ne","ng","nu","no","om","pk","pw","ps","pa","pg","py","pe","ph","pl","pt","pr","qa","ro","ru",
//            "rw","ws","sa","sn","rs","sg","sk","si","so","es","sd","sr","sz","se","ch","sy","tw","tj","tz","th","tg","tk","to",
//            "tt","tn","tr","tm","tv","ug","ua","gb","uy","uz","ve","ye","zm","zw","vn","us","nz","za","ae","as","bj","bv","io",
//            "vg","bf","cv","ky","cx","cc","km","ck","cr","cw","do","dm","sv","gq","fk","gf","pf","gl","gg","gw","hm","hk","im",
//            "ci","ki","xk","ls","mo","mh","mq","nc","nf","kp","mp","pn","kn","lc","pm","vc","sm","sc","sl","sx","sb","gs","ss",
//            "sj","st","tl","tc","vi","vu","va","wf", "eh", "ax"
//    };

    public static String[] countryCodes = Locale.getISOCountries();

}
