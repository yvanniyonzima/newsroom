package comp4905.newsroom.Classes;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Globals {
    //this is the information of the user logged into this device
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
    public static Map<String, String> countryCodes = Stream.of(new Object[][] {
            {"Afghanistan","af"},{"Albania","al"},{"Algeria","dz"},{"Andorra","ad"},{"Angola","ao"},{"Anguilla","ai"},{"Antarctica","aq"},{"AntiguaandBarbuda","ag"},{"Argentina","ar"},{"Armenia","am"},{"Aruba","aw"},{"Australia","au"},{"Austria","at"},{"Azerbaijan","az"},
                    {"Bahamas","bs"},{"Bahrain","bh"},{"Bangladesh","bd"},{"Barbados","bb"},{"Belarus","by"},{"Belgium","be"},{"Belize","bz"},{"Bermuda","bm"},{"Bhutan","bt"},{"Bolivia,PlurinationalStateof","bo"},{"BosniaandHerzegovina","ba"},{"Botswana","bw"},{"Brazil","br"},
                    {"BruneiDarussalam","bn"},{"Bulgaria","bg"},{"Burundi","bi"},{"Cambodia","kh"},{"Cameroon","cm"},{"Canada","ca"},{"Chad","td"},{"Chile","cl"},{"China","cn"},{"Colombia","co"},{"Congo","cg"},{"Croatia","hr"},{"Cuba","cu"},{"Cyprus","cy"},{"Czechia","cz"},
                    {"Denmark","dk"},{"Djibouti","dj"},{"Ecuador","ec"},{"Egypt","eg"},{"Eritrea","er"},{"Estonia","ee"},{"Ethiopia","et"},{"FaroeIslands","fo"},{"Fiji","fj"},{"Finland","fi"},{"France","fr"},{"Gabon","ga"},{"Gambia","gm"},
                    {"Georgia","ge"},{"Germany","de"},{"Ghana","gh"},{"Gibraltar","gi"},{"Greece","gr"},{"Grenada","gd"},{"Guadeloupe","gp"},{"Guam","gu"},{"Guatemala","gt"},{"Guinea","gn"},{"Haiti","ht"},{"Honduras","hn"},{"Hungary","hu"},{"Iceland","is"},{"India","in"},
                    {"Indonesia","id"},{"Iran","ir"},{"Iraq","iq"},{"Ireland","ie"},{"Israel","il"},{"Italy","it"},{"Jamaica","jm"},{"Japan","jp"},{"Jordan","jo"},{"Kazakhstan","kz"},{"Kenya","ke"},{"Korea,Republicof","kr"},{"Kuwait","kw"},
                    {"Kyrgyzstan","kg"},{"SriLanka","lk"},{"LaoPeople'sDemocraticRepublic","la"},{"Latvia","lv"},{"Lebanon","lb"},{"Liberia","lr"},{"Libya","ly"},{"Liechtenstein","li"},{"Lithuania","lt"},{"Luxembourg","lu"},{"NorthMacedonia","mk"},{"Madagascar","mg"},{"Malawi","mw"},{"Malaysia","my"},{"Maldives","mv"},
                    {"Mali","ml"},{"Malta","mt"},{"Mauritania","mr"},{"Mauritius","mu"},{"Mayotte","yt"},{"Mexico","mx"},{"Micronesia,FederatedStatesof","fm"},{"Moldova,Republicof","md"},{"Monaco","mc"},{"Mongolia","mn"},{"Montenegro","me"},{"Montserrat","ms"},{"Morocco","ma"},{"Mozambique","mz"},
                    {"Myanmar","mm"},{"Namibia","na"},{"Nauru","nr"},{"Nepal","np"},{"Netherlands","nl"},{"Nicaragua","ni"},{"Niger","ne"},{"Nigeria","ng"},{"Niue","nu"},{"Norway","no"},{"Oman","om"},{"Pakistan","pk"},
                    {"Palau","pw"},{"Palestine,Stateof","ps"},{"Panama","pa"},{"PapuaNewGuinea","pg"},{"Paraguay","py"},{"Peru","pe"},{"Philippines","ph"},{"Poland","pl"},{"Portugal","pt"},{"PuertoRico","pr"},{"Qatar","qa"},{"Romania","ro"},{"RussianFederation","ru"},{"Rwanda","rw"},{"Samoa","ws"},
                    {"SaudiArabia","sa"},{"Senegal","sn"},{"Serbia","rs"},{"Singapore","sg"},{"Slovakia","sk"},{"Slovenia","si"},{"Somalia","so"},{"Spain","es"},{"Sudan","sd"},{"Suriname","sr"},{"Eswatini","sz"},{"Sweden","se"},{"Switzerland","ch"},{"SyrianArabRepublic","sy"},
                    {"Taiwan,ProvinceofChina","tw"},{"Tajikistan","tj"},{"Tanzania,UnitedRepublicof","tz"},{"Thailand","th"},{"Togo","tg"},{"Tokelau","tk"},{"Tonga","to"},{"TrinidadandTobago","tt"},{"Tunisia","tn"},{"Turkey","tr"},
                    {"Turkmenistan","tm"},{"Tuvalu","tv"},{"Uganda","ug"},{"Ukraine","ua"},{"UnitedKingdom","gb"},{"Uruguay","uy"},{"Uzbekistan","uz"},{"Venezuela,BolivarianRepublicof","ve"},{"Yemen","ye"},{"Zambia","zm"},{"Zimbabwe","zw"},{"VietNam","vn"},{"UnitedStates","us"},{"NewZealand","nz"},{"SouthAfrica","za"},{"UnitedArabEmirates","ae"},{"AmericanSamoa","as"},{"Benin","bj"},
                    {"BouvetIsland","bv"},{"BritishIndianOceanTerritory","io"},{"VirginIslands,British","vg"},{"BurkinaFaso","bf"},{"CaboVerde","cv"},{"CaymanIslands","ky"},{"ChristmasIsland","cx"},{"Cocos(Keeling)Islands","cc"},{"Comoros","km"},{"CookIslands","ck"},{"CostaRica","cr"},{"Curaçao","cw"},
                    {"DominicanRepublic","do"},{"Dominica","dm"},{"ElSalvador","sv"},{"EquatorialGuinea","gq"},{"FalklandIslands(Malvinas)","fk"},{"FrenchGuiana","gf"},{"FrenchPolynesia","pf"},{"Greenland","gl"},{"Guernsey","gg"},
                    {"Guinea-Bissau","gw"},{"HeardIslandandMcDonaldIslands","hm"},{"HongKong","hk"},{"IsleofMan","im"},{"Côted'Ivoire","ci"},{"Kiribati","ki"},{"Kosovo","xk"},{"Lesotho","ls"},{"Macao","mo"},{"MarshallIslands","mh"},{"Martinique","mq"},{"NewCaledonia","nc"},{"NorfolkIsland","nf"},
                    {"Korea,DemocraticPeople'sRepublicof","kp"},{"NorthernMarianaIslands","mp"},{"Pitcairn","pn"},{"SaintKittsandNevis","kn"},{"SaintLucia","lc"},{"SaintPierreandMiquelon","pm"},{"SaintVincentandtheGrenadines","vc"},{"SanMarino","sm"},{"Seychelles","sc"},{"SierraLeone","sl"},{"SintMaarten(Dutchpart)","sx"},
                    {"SolomonIslands","sb"},{"SouthGeorgiaandtheSouthSandwichIslands","gs"},{"SouthSudan","ss"},{"SvalbardandJanMayen","sj"},{"SaoTomeandPrincipe","st"},{"Timor-Leste","tl"},{"TurksandCaicosIslands","tc"},{"VirginIslands,U.S.","vi"},{"Vanuatu","vu"},{"HolySee(VaticanCityState)","va"},{"WallisandFutuna","wf"},{"WesternSahara","eh"},{"ÅlandIslands","ax"}
            }).collect(Collectors.toMap(p -> (String)p[0], p -> (String)p[1]));

    //countries for filter
    public static String[] countries = countryCodes.keySet().toArray(new String[0]);





}
