package com.prayertime.app

data class Dhikr(
    val title: String,
    val arabic: String,
    val hausa: String,
    val repeat: String
)

/**
 * Azkar (supplications) stored inside the app, so they work without internet.
 * NOTE: the Hausa translations should be checked by a knowledgeable person
 * (e.g. a malam / Islamic studies teacher) before the final submission.
 */
object AzkarData {

    private val ayatulKursi = Dhikr(
        "Ayatul Kursi",
        "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        "Allah, babu abin bautawa da gaskiya sai Shi, Mai rai, Mai tsayuwa da kansa. Gyangyaɗi ba ya kama Shi, barci kuma ba ya kama Shi. Abin da yake cikin sammai da abin da yake cikin ƙasa nasa ne. Wane ne zai yi ceto a wurinsa sai da izininsa? Yana sanin abin da yake gabansu da abin da yake bayansu, kuma ba sa kewaye da wani abu daga iliminsa sai abin da Ya so. Kursiyyinsa ya yalwaci sammai da ƙasa, kuma kiyaye su ba ya nauyaya Shi. Kuma Shi ne Maɗaukaki, Mai girma.",
        "Sau 1"
    )

    private val ikhlas = Dhikr(
        "Suratul Ikhlas",
        "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        "Ka ce: Shi Allah ɗaya ne. Allah ne wanda ake nufa da bukatu. Bai haifa ba, kuma ba a haife Shi ba. Kuma babu wani kama a gare Shi.",
        "Sau 3"
    )

    private val falaq = Dhikr(
        "Suratul Falaq",
        "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
        "Ka ce: Ina neman tsari ga Ubangijin alfijir, daga sharrin abin da Ya halitta, da sharrin dare idan ya shiga, da sharrin mata masu busa a cikin kulle-kulle, da sharrin mai hassada idan ya yi hassada.",
        "Sau 3"
    )

    private val nas = Dhikr(
        "Suratun Nas",
        "قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ",
        "Ka ce: Ina neman tsari ga Ubangijin mutane, Sarkin mutane, Abin bautawar mutane, daga sharrin mai waswasi, mai ɓoyewa, wanda yake yin waswasi a cikin ƙirjin mutane, daga aljannu da mutane.",
        "Sau 3"
    )

    private val sayyidul = Dhikr(
        "Sayyidul Istigfar",
        "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي، فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
        "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halicce ni, ni kuma bawanka ne, kuma ina kan alkawarinka da alƙawarinka gwargwadon iyawata. Ina neman tsarinka daga sharrin abin da na aikata. Ina yarda da ni'imarka a kaina, kuma ina amincewa da laifina, don haka Ka gafarta mini, domin babu mai gafarta zunubai sai Kai.",
        "Sau 1"
    )

    private val bismillah = Dhikr(
        "Neman tsari daga cuta",
        "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
        "Da sunan Allah wanda tare da sunansa babu wani abu da yake cutarwa a ƙasa ko a sama, kuma Shi ne Mai ji, Mai sani.",
        "Sau 3"
    )

    private val radeetu = Dhikr(
        "Yarda da Allah, Musulunci da Annabi",
        "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
        "Na yarda da Allah a matsayin Ubangiji, da Musulunci a matsayin addini, da Muhammadu (SAW) a matsayin Annabi.",
        "Sau 3"
    )

    private val hasbiyallah = Dhikr(
        "Hasbiyallah",
        "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
        "Allah ya isar mini, babu abin bautawa da gaskiya sai Shi. A gare Shi na dogara, kuma Shi ne Ubangijin Al'arshi mai girma.",
        "Sau 7"
    )

    private val subhanallahBihamdihi = Dhikr(
        "Subhanallahi wa bihamdihi",
        "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        "Tsarki ya tabbata ga Allah, tare da godiya a gare Shi.",
        "Sau 100"
    )

    private val laIlaha = Dhikr(
        "La ilaha illallahu wahdahu",
        "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        "Babu abin bautawa da gaskiya sai Allah shi kaɗai, ba shi da abokin tarayya. Mulki nasa ne, godiya ma tasa ce, kuma Shi Mai iko ne a kan komai.",
        "Sau 10"
    )

    private val afiya = Dhikr(
        "Neman lafiya",
        "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ",
        "Ya Allah, Ka ba ni lafiya a jikina. Ya Allah, Ka ba ni lafiya a jin kunnena. Ya Allah, Ka ba ni lafiya a ganin idona. Babu abin bautawa da gaskiya sai Kai.",
        "Sau 3"
    )

    val morning: List<Dhikr> = listOf(
        ayatulKursi, ikhlas, falaq, nas,
        Dhikr(
            "Addu'ar safe",
            "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            "Mun wayi gari, mulki ma ya wayi gari na Allah ne, godiya ta Allah ce. Babu abin bautawa da gaskiya sai Allah shi kaɗai, ba shi da abokin tarayya. Mulki nasa ne, godiya ma tasa ce, kuma Shi Mai iko ne a kan komai.",
            "Sau 1"
        ),
        Dhikr(
            "Allahumma bika asbahna",
            "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
            "Ya Allah, da Kai muka wayi gari, da Kai muka yi maraice, da Kai muke rayuwa, da Kai muke mutuwa, kuma zuwa gare Ka ne tashin matattu.",
            "Sau 1"
        ),
        sayyidul, bismillah, radeetu, hasbiyallah, afiya, subhanallahBihamdihi, laIlaha
    )

    val evening: List<Dhikr> = listOf(
        ayatulKursi, ikhlas, falaq, nas,
        Dhikr(
            "Addu'ar maraice",
            "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            "Mun yi maraice, mulki ma ya yi maraice na Allah ne, godiya ta Allah ce. Babu abin bautawa da gaskiya sai Allah shi kaɗai, ba shi da abokin tarayya. Mulki nasa ne, godiya ma tasa ce, kuma Shi Mai iko ne a kan komai.",
            "Sau 1"
        ),
        Dhikr(
            "Allahumma bika amsayna",
            "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
            "Ya Allah, da Kai muka yi maraice, da Kai muka wayi gari, da Kai muke rayuwa, da Kai muke mutuwa, kuma zuwa gare Ka ne makoma.",
            "Sau 1"
        ),
        sayyidul, bismillah, radeetu, hasbiyallah, afiya, subhanallahBihamdihi, laIlaha
    )

    val afterPrayer: List<Dhikr> = listOf(
        Dhikr(
            "Istigfar",
            "أَسْتَغْفِرُ اللَّهَ",
            "Ina neman gafarar Allah.",
            "Sau 3"
        ),
        Dhikr(
            "Allahumma antas salam",
            "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            "Ya Allah, Kai ne Aminci, kuma daga gare Ka ne aminci yake. Ka yawaita albarka, ya Ma'abocin girma da karamci.",
            "Sau 1"
        ),
        Dhikr(
            "Subhanallah",
            "سُبْحَانَ اللَّهِ",
            "Tsarki ya tabbata ga Allah.",
            "Sau 33"
        ),
        Dhikr(
            "Alhamdulillah",
            "الْحَمْدُ لِلَّهِ",
            "Godiya ta tabbata ga Allah.",
            "Sau 33"
        ),
        Dhikr(
            "Allahu Akbar",
            "اللَّهُ أَكْبَرُ",
            "Allah ne Mafi girma.",
            "Sau 33 (ko 34)"
        ),
        ayatulKursi
    )
}
