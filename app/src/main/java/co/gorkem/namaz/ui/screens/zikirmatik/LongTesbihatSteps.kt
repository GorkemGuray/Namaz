package co.gorkem.namaz.ui.screens.zikirmatik
object LongTesbihatSteps {
    val sabahSteps = listOf(
        NamazTesbihatiStep(
            title = "1. İstiğfar ve Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme ente’s-selâmü ve minke’s-selâm, tebârakte yâ ze’l-celâli ve’l-ikrâm.",
            meaning = "Allah’ım! Sen Selâm’sın; selâm ve selâmet Senden gelir. Her şeyden üstün ve yücesin ey Celal ve İkram sahibi!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "2. Salâten Tüncînâ",
            textArabic = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Allâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed. Salâten tüncînâ bihâ min cemîı’l-ehvâli ve’l-âfât. Ve takdî lenâ bihâ cemîa’l-hâcât. Ve tutahhirunâ bihâ min cemîı’s-seyyiât. Ve terfeunâ bihâ ındeke a’le’d-derecât. Ve tübelliğunâ bihâ aksa’l-gâyât. Min cemîı’l-hayrâti fi’l-hayâti ve ba’de’l-memât. Âmin yâ mücîbe’d-deavât ve’l-hamdü lillâhi rabbi’l-âlemîn.",
            meaning = "Allah’ım! Efendimiz Muhammed’e ve âline öyle bir salât ve rahmet eyle ki; onunla bizleri bütün korku ve afetlerden kurtar, bütün ihtiyaçlarımızı yerine getir, bizleri günahlardan arındır, katında en yüce derecelere yükselt, hayatta ve ölümden sonra bütün hayırların en son gayesine ulaştır. Âmin, ey dualara icabet eden! Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "\"ve’l-âfât\" derken avuç içleri yere çevrilir, bitince tekrar yukarı çevrilir."
        ),
        NamazTesbihatiStep(
            title = "3. Tevhid (10 Defa)",
            textArabic = "اَللّٰهُمَّ اِنَّا نُقَدِّمُ اِلَيْكَ بَيْنَ يَدَىْ كُلِّ نَفَسٍ وَ لَمْحَةٍ وَ لَحْظَةٍ وَ طَرْفَةٍ يَطْرِفُ بِهَٓا اَهْلُ السَّمٰوَاتِ وَ اَهْلُ اْلاَرَض۪ينَ شَهَادَةً اَشْهَدُ اَنْ:\n\nلآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ",
            textTurkish = "Allâhümme innâ nukaddimü ileyke beyne yedey külli nefesin ve lemhatin ve lâhzatin ve tarfetin yatrifu bihâ ehlü’s-semâvâti ve ehlü’l-aradîne şehâdeten: Eşhedü en lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, ve hüve hayyün lâ yemût, biyedihi’l-hayr ve hüve alâ külli şey’in kadîr. (10. tekrarın sonunda 've ileyhi’l-masîr' eklenir).",
            meaning = "Allah’ım! Her nefeste, her anda gökler ve yer ehlinin her göz açıp kapamasında şehadet ederiz ki: Allah’tan başka ilâh yoktur; O tektir, şeriki yoktur. Mülk O'nundur, hamd O'na mahsustur. Hayatı veren ve öldüren O'dur. O ölümsüz diridir, bütün hayır O'nun elindedir ve O her şeye kadirdir. Dönüş de O'nadır.",
            type = StepType.COUNTER,
            targetCount = 10
        ),
        NamazTesbihatiStep(
            title = "4. İstiâze (7 Defa)",
            textArabic = "اَللّٰهُمَّ اَجِرْنَا مِنَ النَّارِ",
            textTurkish = "Allâhümme ecirnâ mine’n-nâr.",
            meaning = "Allah’ım! Bizi cehennem ateşinden kurtar.",
            type = StepType.COUNTER,
            targetCount = 7,
            handGesture = "Avuç içleri aşağıya (yere bakacak şekilde) tutulur."
        ),
        NamazTesbihatiStep(
            title = "5. Ateşten Korunma Duası",
            textArabic = "اَللّٰهُمَّ اَجِرْنَا مِنْ كُلِّ نَارٍ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الدّ۪ينِيَّةِ وَ الدُّنْيَوِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ اٰخِرِ الزَّمَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْمَس۪يحِ الدَّجَّالِ وَ السُّفْيَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الضَّلاَلاَتِ وَ الْبِدْعِيَّاتِ وَ الْبَلِيَّاتِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النَّفْسِ اْلاَمَّارَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شُرُورِ النُّفُوسِ اْلاَمَّارَاتِ الْفِرْعَوْنِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ بَلآَءِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ يَوْمِ الْقِيٰمَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ جَهَنَّمَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ نَارِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ وَ النِّرَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الرِّيَاءِ وَ السُّمْعَةِ وَ الْعُجُبِ وَ الْفَخْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ تَجَاوُزِ الْمُلْحِد۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ الْمُنَافِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْفَاسِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ ف۪ى خِدْمَةِ الْقُرْاٰنِ وَ اْلا۪يمَانِ وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا مِنَ النَّارِ",
            textTurkish = "Allâhümme ecirnâ min külli nâr. Allâhümme ecirnâ min fitneti’d-dîniyyeti ve’d-dünyeviyyeh. Allâhümme ecirnâ min fitneti âhiri’z-zamân. Allâhümme ecirnâ min fitneti Mesîhi’d-Deccâli ve’s-Süfyân. Allâhümme ecirnâ mine’d-dalâlâti ve’l-bid’ıyyâti ve’l-beliyyât. Allâhümme ecirnâ min şerri’n-nefsi’l-emmâreh. Allâhümme ecirnâ min şürûri’n-nüfûsi’l-emmârâti’l-fir’avniyyeh. Allâhümme ecirnâ min şerri’n-nisâ. Allâhümme ecirnâ min belâi’n-nisâ. Allâhümme ecirnâ min fitneti’n-nisâ. Allâhümme ecirnâ min azâbi’l-kabr. Allâhümme ecirnâ min azâbi yevmi’l-kıyâmeh. Allâhümme ecirnâ min azâbi cehennem. Allâhümme ecirnâ min azâbi kahrik. Allâhümme ecirnâ min nâri kahrik. Allâhümme ecirnâ min azâbi’l-kabri ve’n-nîrân. Allâhümme ecirnâ mine’r-riyâi ve’s-süm’ati ve’l-ucubi ve’l-fahr. Allâhümme ecirnâ min tecâvüzi’l-mülhidîn. Allâhümme ecirnâ min şerri’l-münâfikîn. Allâhümme ecirnâ min fitneti’l-fâsikîn. Allâhümme ecirnâ ve ecir vâlideynâ ve talebete Resâili’n-Nûri’s-sâdikîne fî hidmeti’l-Kur’âni ve’l-îmân. Ve ahbâbene’l-mü’minîne’l-muhlisîne ve akribâenâ ve ecdâdenâ mine’n-nâr.",
            meaning = "Allah’ım! Bizi her çeşit ateşten kurtar. Dini ve dünyevi fitnelerden muhafaza eyle. Ahir zaman fitnesinden koru. Deccal ve Süfyan fitnesinden, sapkınlıklardan, bidatlardan ve belalardan halas eyle. Kötülüğü emreden nefsin şerrinden, kabir, kıyamet ve cehennem azabından kurtar. Riya, şöhret arzusu ve kibirden muhafaza eyle. Bizi, anne-babamızı ve bütün mümin kardeşlerimizi cehennem ateşinden halas eyle.",
            type = StepType.PRAYER,
            handGesture = "Avuç içleri aşağıya (yere bakacak şekilde) tutulmaya devam edilir."
        ),
        NamazTesbihatiStep(
            title = "6. Cennet Duası",
            textArabic = "بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا وَ اَدْخِلْ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ ف۪ى خِدْمَةِ اْلا۪يمَانِ وَ الْقُرْاٰنِ اَلْجَنَّةَ مَعَ اْلاَبْرَارِ صَلِّ عَلٰى نَبِيِّكَ الْمُخْتَارِ وَ اٰلِه۪ اْلاَطْهَارِ وَ اَصْحَابِهِ اْلاَخْيَارِ وَ سَلِّمْ مَادَامَ الَّيْلُ وَ النَّهَارُ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bi-afvike yâ Mücîr, bi-fadlike yâ Gaffâr. Allâhümme edhilne’l-Cennete mea’l-ebrâr. Allâhümme edhilne’l-Cennete mea’l-ebrâr. Allâhümme edhilnâ ve edhil vâlideynâ ve ihvânenâ ve ehavâtinâ ve akribâenâ ve ecdâdenâ ve ahbâbene’l-mü’minîne’l-muhlisîne fî hidmeti’l-îmâni ve’l-Kur’ân. El-Cennete mea’l-ebrâr bi-şefâati nebiyyike’l-muhtâr ve âlihi’l-ethâr ve ashâbihi’l-ahyâr ve sellim mâ dâme’l-leylü ve’n-nehâr. Âmîn ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Affınla kabul buyur ey kurtaran Mücîr! Fazlınla bağışla ey günahları örten Gaffâr! Allah’ım! Bizleri ebrâr olan salih kullarınla beraber Cennet’e idhal eyle. Bizi, ana-babamızı, iman ve Kur'an yolundaki kardeşlerimizi, Peygamberimiz’in şefaati ve ashabının hürmetine Cennet’e nail eyle. Gece ve gündüz devam ettikçe onlara selâm olsun. Âmin, Âlemlerin Rabbi olan Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "Avuç içleri tekrar yukarı çevrilerek semaya açılır."
        ),
        NamazTesbihatiStep(
            title = "7. Tesbih ve Tevhid",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Sübhânallâhi vel-hamdü lillâhi ve lâ ilâhe illallâhü vallâhü ekber. Ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm.",
            meaning = "Allah’ı bütün noksan sıfatlardan tenzih ederim. Hamd Allah’adır. Allah’tan başka ilâh yoktur ve Allah en büyüktür. Güç ve kuvvet ancak yüce ve azamet sahibi Allah’tandır.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "8. Âyet-el Kürsî",
            textArabic = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
            textTurkish = "Allâhü lâ ilâhe illâ hüve’l-hayyü’l-kayyûm. Lâ te’huzühû sinetün ve lâ nevm. Lehû mâ fi’s-semâvâti ve mâ fi’l-ard. Men zellezî yeşfeu ındehû illâ bi-iznih. Ya’lemü mâ beyne eydîhim ve mâ halfehüm. Ve lâ yuhîtûne bi-şey’in min ılmihî illâ bimâ şâ’. Vesi’a kürsiyyühü’s-semâvâti ve’l-ard. Ve lâ yeûdühû hıfzuhümâ ve hüve’l-aliyyü’l-azîm.",
            meaning = "Allah, O’ndan başka ilâh yoktur; diridir, her şeyin varlığı Kendisine bağlıdır. Kendisine ne uyuklama ne de uyku gelir. Göklerde ve yerde ne varsa O’nundur. İzni olmaksızın O’nun katında kim şefaat edebilir? Kulların yaptıklarını ve yapacaklarını bilir. O’nun ilminden, Kendisinin dilediği müstesna hiçbir şeyi kavrayamazlar. Kürsüsü gökleri ve yeri kuşatmıştır. Onları koruyup gözetmek O’na asla ağır gelmez. O çok yücedir, çok büyüktür.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "9. Sübhânallah (33 Defa)",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüve’l-aliyyü’l-azîmü zü’l-celâli sübhânallâh.",
            meaning = "O çok yücedir, çok büyüktür; celâl sahibi Allah her türlü noksanlıktan münezzehtir.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "10. Elhamdülillâh (33 Defa)",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübhâne’l-bâkî dâimenil-hamdülillâh.",
            meaning = "Varlığı ebedî olan Allah noksan sıfatlardan uzaktır; daima hamd Allah’a mahsustur.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "11. Allâhuekber (33 Defa)",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbi’l-âlemîne teâlâ şânühü Allâhü ekber.",
            meaning = "Âlemlerin Rabbi olan Allah’ın şânı ne yücedir; Allah en büyüktür.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "12. Tahlil ve Dua",
            textArabic = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
            textTurkish = "Lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, ve hüve hayyün lâ yemût, biyedihi’l-hayru ve hüve alâ külli şey’in kadîr ve ileyhi’l-masîr.",
            meaning = "Allah’tan başka ilâh yoktur; O tektir, ortağı yoktur. Mülk O’nundur, hamd O’na mahsustur. Hayatı veren ve öldüren O’dur. O hiç ölmeyen Hayy’dır. Bütün hayır O’nun elindedir ve O her şeye hakkıyla kādirdir. Dönüş de O’nadır. (Dua vakti: Ellerinizi açarak dua ediniz).",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "13. Kelime-i Tevhid (33 Defa)",
            textArabic = "فَاعْلَمْ أَنَّهُ: لَا إِلٰهَ إِلَّا اللهُ",
            textTurkish = "Fa’lem ennehû: Lâ ilâhe illallâh. (33 defa tekrar edilir. Sonunda: Muhammedü’r-resûlüllâhi sallâllâhü teâlâ aleyhi ve sellem).",
            meaning = "Bil ki: Allah'tan başka ilâh yoktur! (33 defa zikredilir. Sonunda 'Muhammed Allah’ın elçisidir, Allah ona salât ve selâm eylesin' denir).",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "14. Sabah Tevhidi (10 Defa)",
            textArabic = "لآَ اِلٰهَ اِلاَّ اللّٰهُ اَلْمَلِكُ الْحَقُّ الْمُب۪ينُ مُحَمَّدٌ رَسُولُ اللّٰهِ صَادِقُ الْوَعْدِ اْلاَم۪ينُ",
            textTurkish = "Lâ ilâhe illallâhü’l-Melikü’l-Hakku’l-Mübîn, Muhammedü’r-resûlüllâhi sâdiku’l-va’di’l-emîn.",
            meaning = "Mülk sahibi, hak ve apaçık olan Allah’tan başka ilâh yoktur. Sözünde sadık ve güvenilir olan Muhammed (a.s.m) O’nun elçisidir.",
            type = StepType.COUNTER,
            targetCount = 10
        ),
        NamazTesbihatiStep(
            title = "15. Salavat-ı Şerife",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا (٣ defa)\n\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ\n\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا رَسُولَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا حَب۪يبَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ\n\nاَللّٰهُمَّ صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِوَالِدَيْنَا وَ بِجَم۪يعِ الْمُؤْمِن۪ينَ وَ الْمُؤْمِنَاتِ يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nİnnallâhe ve melâiketehû yüsallûne ale’n-nebiy. Yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk.\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, biadedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. (3 defa)\n\nSalli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemîı’l-enbiyâi ve’l-mürselîn ve alâ âli küllin ve sahbi küllin ecmaîn. Âmin ve’l-hamdü lillâhi Rabbi’l-âlemîn.\n\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Resûlallâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Habîballâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ emîne vahyillâh.\n\nAllâhümme salli ve sellim ve bârik alâ seyyidinâ Muhammedin ve alâ âlihî ve ashâbihî biadedi evrâkı’l-eşcâr ve emvâci’l-bihâr ve katarâti’l-emtâr. Vagfir lenâ verhamnâ veltuf binâ ve bivâlideynâ ve bi-cemîı’l-mü’minîne ve’l-mü’minât yâ ilâhenâ bikülli salâtin minhâ. Eşhedü en lâ ilâhe illallâh ve eşhedü enne Muhammeder-resûlüllâh sallâllâhü teâlâ aleyhi ve sellem.",
            meaning = "Şüphesiz Allah ve melekleri Peygamber’e salât ederler. Ey iman edenler, siz de ona tam bir teslimiyetle salât ve selâm getirin! Allah’ım! Efendimiz Muhammed’e ve âline bütün dertler ve devalar adedince salât, bereket ve selâm eyle. Ağaçların yaprakları, denizlerin dalgaları ve yağmurun damlaları adedince salât ve selâm olsun. Milyonlarca salât ve selâm senin üzerine olsun ey Allah’ın Resulü!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "16. Tercüman-ı İsm-i A'zâm Duası",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nسُبْحَانَكَ يَٓا اَللّٰهُ تَعَالَيْتَ يَا رَحْمٰنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا رَح۪يمُ تَعَالَيْتَ يَا كَر۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَم۪يدُ تَعَالَيْتَ يَا حَك۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مَج۪يدُ تَعَالَيْتَ يَا مَلِكُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قُدُّوسُ تَعَالَيْتَ يَا سَلاَمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُؤْمِنُ تَعَالَيْتَ يَا مُهَيْمِنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَز۪يزُ تَعَالَيْتَ يَا جَبَّارُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُتَكَبِّرُ تَعَالَيْتَ يَا خَالِقُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَٓا اَوَّلُ تَعَالَيْتَ يَٓا اٰخِرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا ظَاهِرُ تَعَالَيْتَ يَا بَاطِنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا بَارِىءُ تَعَالَيْتَ يَا مُصَوِّرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا تَوَّابُ تَعَالَيْتَ يَا وَهَّابُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا بَاعِثُ تَعَالَيْتَ يَا وَارِثُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قَد۪يمُ تَعَالَيْتَ يَا مُق۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا فَرْدُ تَعَالَيْتَ يَا وِتْرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا نُورُ تَعَالَيْتَ يَا سَتَّارُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا جَل۪يلُ تَعَالَيْتَ يَا جَم۪يلُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قَاهِرُ تَعَالَيْتَ يَا قَادِرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مَل۪يكُ تَعَالَيْتَ يَا مُقْتَدِرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَل۪يمُ تَعَالَيْتَ يَا عَلاَّمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَظ۪يمُ تَعَالَيْتَ يَا غَفُورُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَل۪يمُ تَعَالَيْتَ يَا وَدُودُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا شَه۪يدُ تَعَالَيْتَ يَا شَاهِدُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا كَب۪يرُ تَعَالَيْتَ يَا مُتَعَالُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا نُورُ تَعَالَيْتَ يَا لَط۪يفُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا سَم۪يعُ تَعَالَيْتَ يَا كَف۪يلُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قَر۪يبُ تَعَالَيْتَ يَا بَص۪يرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَقُّ تَعَالَيْتَ يَا مُب۪ينُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا رَؤُفُ تَعَالَيْتَ يَا رَح۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا طَاهِرُ تَعَالَيْتَ يَا مُطَهِّرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُجَمِّلُ تَعَالَيْتَ يَا مُفَضِّلُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُظْهِرُ تَعَالَيْتَ يَا مُنْعِمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا دَيَّانُ تَعَالَيْتَ يَا سُلْطَانُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَنَّانُ تَعَالَيْتَ يَا مَنَّانُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَٓا اَحَدُ تَعَالَيْتَ يَا صَمَدُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَىُّ تَعَالَيْتَ يَا قَيُّومُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَدْلُ تَعَالَيْتَ يَا حَكَمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا فَرْدُ تَعَالَيْتَ يَا قُدُّوسُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\n\nسُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ جَم۪يعَ الْمُؤْمِن۪ينَ مِنَ النَّارِ ٭\nوَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ ٭\nبِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nSübhâneke yâ Allâh teâleyte yâ Rahmân ecirnâ mine’n-nâr bi-afvike yâ Rahmân.\nSübhâneke yâ Rahîm teâleyte yâ Kerîm ecirnâ mine’n-nâr bi-afvike yâ Rahmân...\n(Dua devam eder...)\nSübhâneke âhiyyen şerâhiyyen teâleyte lâ ilâhe illâ ente ecirnâ ve ecir vâlideynâ ve ihvânenâ ve ehavâtinâ ve cemîa’l-mü’minîne mine’n-nâr.\nVe min külli nâr, vahfeznâ min şerri’n-nefsi ve’ş-şeytân ve min şerri’l-cinni ve’l-insân ve min şerri’l-bid’ati ve’d-dalâlâti ve’l-ilhâdi ve’t-tuğyân.\nBi-afvike yâ Mücîr, bi-fadlike yâ Gaffâr, bi-rahmetike yâ Erhame’r-râhimîn. Allâhümme edhilne’l-Cennete mea’l-ebrâr bi-şefâati nebiyyike’l-muhtâr. Âmîn, ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Sen her noksandan münezzehsin ey Allah! Sen her şeyden yücesin ey Rahmân! Affınla bizi cehennem ateşinden koru ey merhameti sonsuz olan! (Allah’ın güzel isimleri ile cehennem ateşinden, nefsin, şeytanın, cinnin ve insanların şerrinden, bidat ve sapkınlıklardan Allah’a sığınma ve cenneti talep etme duası).",
            type = StepType.PRAYER,
            handGesture = "\"Sübhâneke âhiyyen...\" kısmında eller açık; \"Ve min külli nâr...\" kısmında avuçlar aşağı çevrilir; \"Bi-afvike...\" kısmında tekrar yukarı açılır."
        ),
        NamazTesbihatiStep(
            title = "17. Haşr Sûresi (20-24. Âyetler)",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nلاَ يَسْتَو۪ٓى اَصْحَابُ النَّارِ وَاَصْحَابُ الْجَنَّةِۜ اَصْحَابُ الْجَنَّةِ هُمُ الْفَٓائِزُونَ ﴿٢٠﴾\nلَوْ اَنْزَلْنَا هٰذَا الْقُرْاٰنَ عَلٰى جَبَلٍ لَرَاَيْتَهُ خَاشِعًا مُتَصَدِّعًا مِنْ خَشْيَةِ اللّٰهِۜ وَتِلْكَ اْلاَمْثَالُ نَضْرِبُهَا لِلنَّاسِ لَعَلَّهُمْ يَتَفَكَّرُونَ ﴿٢١﴾\nهُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ عَالِمُ الْغَيْبِ وَالشَّهَادَةِۚ هُوَ الرَّحْمٰنُ الرَّح۪يمُ ﴿٢٢﴾\nهُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ اَلْمَلِكُ الْقُدُّوسُ السَّلاَمُ الْمُؤْمِنُ الْمُهَيْمِنُ الْعَز۪يزُ الْجَبَّارُ الْمُتَكَبِّرُۜ سُبْحَانَ اللّٰهِ عَمَّا يُشْرِكُونَ ﴿٢٣﴾\nهُوَ اللّٰهُ الْخَالِقُ الْبَارِىُٔ الْمُصَوِّرُ لَهُ اْلاَسْمَٓاءُ الْحُسْنٰىۜ يُسَبِّحُ لَهُ مَا فِى السَّمٰوَاتِ وَاْلاَرْضِۚ وَهُوَ الْعَز۪يزُ الْحَك۪يمُ ﴿٢٤﴾",
            textTurkish = "Bismillâhirrahmânirrahîm.\nLâ yestevî ashâbü’n-nâri ve ashâbü’l-Cenneh, ashâbü’l-Cenneti hümü’l-fâizûn.\nLev enzelnâ hâze’l-Kur’âne alâ cebelin leraeytehû hâşi’an mütasaddian min haşyetillâh, ve tilke’l-emsâlü nadribühâ lin-nâsi leallehüm yetefekkerûn.\nHüvallâhüllezî lâ ilâhe illâ hû, âlimü’l-gaybi ve’ş-şehâdeh, hüve’r-Rahmânü’r-Rahîm.\nHüvallâhüllezî lâ ilâhe illâ hû, el-Melikü’l-Kuddûsü’s-Selâmü’l-Mü’minü’l-Müheyminü’l-Azîzü’l-Cebbârü’l-Mütekebbir, sübhânallâhi ammâ yüşrikûn.\nHüvallâhü’l-Hâliku’l-Bâriü’l-Musavviru lehü’l-Esmâü’l-Hüsnâ, yüsebbihu lehû mâ fi’s-semâvâti ve’l-ard, ve hüve’l-Azîzü’l-Hakîm.",
            meaning = "Cehennemliklerle cennetlikler bir olmaz; kurtuluşa erenler cennetliklerdir. Eğer biz bu Kur’an’ı bir dağa indirseydik, Allah korkusundan boyun eğip paramparça olduğunu görürdün... O Allah ki, O'ndan başka ilâh yoktur. Gaybı da görünen âlemi de bilendir; O Rahmân’dır, Rahîm’dir. O Melik, Kuddûs, Selâm, Mü’min, Müheymin, Azîz, Cebbâr, Mütekebbir’dir. En güzel isimler O’nundur.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "18. Fâtiha Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbi’l-âlemîn. Er-rahmâni’r-rahîm. Mâliki yevmi’d-dîn. İyyâke na’büdü ve iyyâke neste’în. İhdina’s-sırâta’l-müstakîm. Sırâtallezîne en’amte aleyhim gayri’l-magdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Âlemlerin Rabbi Allah'a mahsustur. O, Rahman'dır, Rahim'dir. Hesap ve ceza gününün mâlikidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet; kendilerine nimet verdiklerinin yoluna, gazaba uğrayanların ve sapanlarınkine değil.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "19. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah dualarınızı, zikirlerinizi ve tesbihatınızı kabul eylesin!",
            type = StepType.COMPLETED
        )
    )

    val ogleSteps = listOf(
        NamazTesbihatiStep(
            title = "1. İstiğfar ve Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme ente’s-selâmü ve minke’s-selâm, tebârakte yâ ze’l-celâli ve’l-ikrâm.",
            meaning = "Allah’ım! Sen Selâm’sın; selâm ve selâmet Senden gelir. Her şeyden üstün ve yücesin ey Celal ve İkram sahibi!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "2. Salâten Tüncînâ",
            textArabic = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Allâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed. Salâten tüncînâ bihâ min cemîı’l-ehvâli ve’l-âfât. Ve takdî lenâ bihâ cemîa’l-hâcât. Ve tutahhirunâ bihâ min cemîı’s-seyyiât. Ve terfeunâ bihâ ındeke a’le’d-derecât. Ve tübelliğunâ bihâ aksa’l-gâyât. Min cemîı’l-hayrâti fi’l-hayâti ve ba’de’l-memât. Âmin yâ mücîbe’d-deavât ve’l-hamdü lillâhi rabbi’l-âlemîn.",
            meaning = "Allah’ım! Efendimiz Muhammed’e ve âline öyle bir salât ve rahmet eyle ki; onunla bizleri bütün korku ve afetlerden kurtar, bütün ihtiyaçlarımızı yerine getir, bizleri günahlardan arındır, katında en yüce derecelere yükselt, hayatta ve ölümden sonra bütün hayırların en son gayesine ulaştır. Âmin, ey dualara icabet eden! Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "\"ve’l-âfât\" derken avuç içleri yere çevrilir, bitince tekrar yukarı çevrilir."
        ),
        NamazTesbihatiStep(
            title = "3. Tesbih ve Tevhid",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Sübhânallâhi vel-hamdü lillâhi ve lâ ilâhe illallâhü vallâhü ekber. Ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm.",
            meaning = "Allah’ı bütün noksan sıfatlardan tenzih ederim. Hamd Allah’adır. Allah’tan başka ilâh yoktur ve Allah en büyüktür. Güç ve kuvvet ancak yüce ve azamet sahibi Allah’tandır.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "4. Âyet-el Kürsî",
            textArabic = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
            textTurkish = "Allâhü lâ ilâhe illâ hüve’l-hayyü’l-kayyûm. Lâ te’huzühû sinetün ve lâ nevm. Lehû mâ fi’s-semâvâti ve mâ fi’l-ard. Men zellezî yeşfeu ındehû illâ bi-iznih. Ya’lemü mâ beyne eydîhim ve mâ halfehüm. Ve lâ yuhîtûne bi-şey’in min ılmihî illâ bimâ şâ’. Vesi’a kürsiyyühü’s-semâvâti ve’l-ard. Ve lâ yeûdühû hıfzuhümâ ve hüve’l-aliyyü’l-azîm.",
            meaning = "Allah, O’ndan başka ilâh yoktur; diridir, her şeyin varlığı Kendisine bağlıdır. Kendisine ne uyuklama ne de uyku gelir. Göklerde ve yerde ne varsa O’nundur. İzni olmaksızın O’nun katında kim şefaat edebilir? Kulların yaptıklarını ve yapacaklarını bilir. O’nun ilminden, Kendisinin dilediği müstesna hiçbir şeyi kavrayamazlar. Kürsüsü gökleri ve yeri kuşatmıştır. Onları koruyup gözetmek O’na asla ağır gelmez. O çok yücedir, çok büyüktür.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "5. Sübhânallah (33 Defa)",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüve’l-aliyyü’l-azîmü zü’l-celâli sübhânallâh.",
            meaning = "O çok yücedir, çok büyüktür; celâl sahibi Allah her türlü noksanlıktan münezzehtir.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "6. Elhamdülillâh (33 Defa)",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübhâne’l-bâkî dâimenil-hamdülillâh.",
            meaning = "Varlığı ebedî olan Allah noksan sıfatlardan uzaktır; daima hamd Allah’a mahsustur.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "7. Allâhuekber (33 Defa)",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbi’l-âlemîne teâlâ şânühü Allâhü ekber.",
            meaning = "Âlemlerin Rabbi olan Allah’ın şânı ne yücedir; Allah en büyüktür.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "8. Tahlil ve Dua",
            textArabic = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
            textTurkish = "Lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, ve hüve hayyün lâ yemût, biyedihi’l-hayru ve hüve alâ külli şey’in kadîr ve ileyhi’l-masîr.",
            meaning = "Allah’tan başka ilâh yoktur; O tektir, ortağı yoktur. Mülk O’nundur, hamd O’na mahsustur. Hayatı veren ve öldüren O’dur. O hiç ölmeyen Hayy’dır. Bütün hayır O’nun elindedir ve O her şeye hakkıyla kādirdir. Dönüş de O’nadır. (Dua vakti: Ellerinizi açarak dua ediniz).",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "9. Kelime-i Tevhid (33 Defa)",
            textArabic = "فَاعْلَمْ أَنَّهُ: لَا إِلٰهَ إِلَّا اللهُ",
            textTurkish = "Fa’lem ennehû: Lâ ilâhe illallâh. (33 defa tekrar edilir. Sonunda: Muhammedü’r-resûlüllâhi sallâllâhü teâlâ aleyhi ve sellem).",
            meaning = "Bil ki: Allah'tan başka ilâh yoktur! (33 defa zikredilir. Sonunda 'Muhammed Allah’ın elçisidir, Allah ona salât ve selâm eylesin' denir).",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "10. Salavat-ı Şerife",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا (٣ defa)\n\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ\n\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا رَسُولَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا حَب۪يبَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ\n\nاَللّٰهُمَّ صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِوَالِدَيْنَا وَ بِجَم۪يعِ الْمُؤْمِن۪ينَ وَ الْمُؤْمِنَاتِ يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nİnnallâhe ve melâiketehû yüsallûne ale’n-nebiy. Yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk.\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, biadedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. (3 defa)\n\nSalli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemîı’l-enbiyâi ve’l-mürselîn ve alâ âli küllin ve sahbi küllin ecmaîn. Âmin ve’l-hamdü lillâhi Rabbi’l-âlemîn.\n\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Resûlallâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Habîballâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ emîne vahyillâh.\n\nAllâhümme salli ve sellim ve bârik alâ seyyidinâ Muhammedin ve alâ âlihî ve ashâbihî biadedi evrâkı’l-eşcâr ve emvâci’l-bihâr ve katarâti’l-emtâr. Vagfir lenâ verhamnâ veltuf binâ ve bivâlideynâ ve bi-cemîı’l-mü’minîne ve’l-mü’minât yâ ilâhenâ bikülli salâtin minhâ. Eşhedü en lâ ilâhe illallâh ve eşhedü enne Muhammeder-resûlüllâh sallâllâhü teâlâ aleyhi ve sellem.",
            meaning = "Şüphesiz Allah ve melekleri Peygamber’e salât ederler. Ey iman edenler, siz de ona tam bir teslimiyetle salât ve selâm getirin! Allah’ım! Efendimiz Muhammed’e ve âline bütün dertler ve devalar adedince salât, bereket ve selâm eyle. Ağaçların yaprakları, denizlerin dalgaları ve yağmurun damlaları adedince salât ve selâm olsun. Milyonlarca salât ve selâm senin üzerine olsun ey Allah’ın Resulü!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "11. İsm-i A'zâm Duası",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nيَا جَم۪يلُ يَٓا اَللّٰهُ ٭ يَا قَر۪يبُ يَٓا اَللّٰهُ\nيَا مُج۪يبُ يَٓا اَللّٰهُ ٭ يَا حَب۪يبُ يَٓا اَللّٰهُ\nيَا رَؤُفُ يَٓا اَللّٰهُ ٭ يَا عَطُوفُ يَٓا اَللّٰهُ\nيَا مَعْرُوفُ يَٓا اَللّٰهُ ٭ يَا لَط۪يفُ يَٓا اَللّٰهُ\nيَا عَظ۪يمُ يَٓا اَللّٰهُ ٭ يَا حَنَّانُ يَٓا اَللّٰهُ\nيَا مَنَّانُ يَٓا اَللّٰهُ ٭ يَا دَيَّانُ يَٓا اَللّٰهُ\nيَا سُبْحَانُ يَٓا اَللّٰهُ ٭ يَٓا اَمَانُ يَٓا اَللّٰهُ\nيَا بُرْهَانُ يَٓا اَللّٰهُ ٭ يَا سُلْطَانُ يَٓا اَللّٰهُ\nيَا مُسْتَعَانُ يَٓا اَللّٰهُ ٭ يَا مُحْسِنُ يَٓا اَللّٰهُ\nيَا مُتَعَالُ يَٓا اَللّٰهُ ٭ يَا رَحْمٰنُ يَٓا اَللّٰهُ\nيَا رَح۪يمُ يَٓا اَللّٰهُ ٭ يَا كَر۪يمُ يَٓا اَللّٰهُ\nيَا مَج۪يدُ يَٓا اَللّٰهُ ٭ يَا فَرْدُ يَٓا اَللّٰهُ\nيَا وِتْرُ يَٓا اَللّٰهُ ٭ يَٓا اَحَدُ يَٓا اَللّٰهُ\nيَا صَمَدُ يَٓا اَللّٰهُ ٭ يَا مَحْمُودُ يَٓا اَللّٰهُ\nيَا صَادِقَ الْوَعْدِ يَٓا اَللّٰهُ ٭ يَا عَلِىُّ يَٓا اَللّٰهُ\nيَا غَنِىُّ يَٓا اَللّٰهُ ٭ يَا شَاف۪ى يَٓا اَللّٰهُ\nيَا كَاف۪ى يَٓا اَللّٰهُ ٭ يَا مُعَاف۪ى يَٓا اَللّٰهُ\nيَا بَاق۪ى يَٓا اَللّٰهُ ٭ يَا هَاد۪ى يَٓا اَللّٰهُ\nيَا قَادِرُ يَٓا اَللّٰهُ ٭ يَا سَاتِرُ يَٓا اَللّٰهُ\nيَا قَهَّارُ يَٓا اَللّٰهُ ٭ يَا جَبَّارُ يَٓا اَللّٰهُ\nيَا غَفَّارُ يَٓا اَللّٰهُ ٭ يَا فَتَّاحُ يَٓا اَللّٰهُ\n\nيَا رَبَّ السَّمٰوَاتِ وَ اْلاَرْضِ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ اَسْئَلُكَ بِحَقِّ هٰذِهِ اْلاَسْمَٓاءِ كُلِّهَٓا اَنْ تُصَلِّىَ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلٰٓى اٰلِ مُحَمَّدٍ وَ ارْحَمْ مُحَمَّدًا كَمَا صَلَّيْتَ وَ سَلَّمْتَ وَ بَارَكْتَ وَ رَحِمْتَ وَ تَرَحَّمْتَ عَلٰٓى اِبرَاه۪يمَ وَ عَلٰٓى اٰلِ اِبْرَاه۪يمَ فِى الْعَالَم۪ينَ رَبَّنَٓا اِنَّكَ حَم۪يدٌ مَج۪يدٌ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ وَ الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nYâ Cemîlü yâ Allâh, yâ Karîbü yâ Allâh.\nYâ Mücîbü yâ Allâh, yâ Habîbü yâ Allâh.\nYâ Raûfü yâ Allâh, yâ Atûfü yâ Allâh.\nYâ Ma’rûfü yâ Allâh, yâ Latîfü yâ Allâh.\nYâ Azîmü yâ Allâh, yâ Hannânü yâ Allâh.\nYâ Mennânü yâ Allâh, yâ Deyyânü yâ Allâh.\nYâ Sübhânü yâ Allâh, yâ Emânü yâ Allâh.\nYâ Burhânü yâ Allâh, yâ Sultânü yâ Allâh.\nYâ Müsteânü yâ Allâh, yâ Muhsinü yâ Allâh.\nYâ Müteâlü yâ Allâh, yâ Rahmânü yâ Allâh.\nYâ Rahîmü yâ Allâh, yâ Kerîmü yâ Allâh.\nYâ Mecîdü yâ Allâh, yâ Ferdü yâ Allâh.\nYâ Vitrü yâ Allâh, yâ Ehadü yâ Allâh.\nYâ Samedü yâ Allâh, yâ Mahmûdü yâ Allâh.\nYâ Sâdıka’l-va’di yâ Allâh, yâ Aliyyü yâ Allâh.\nYâ Ganiyyü yâ Allâh, yâ Şâfî yâ Allâh.\nYâ Kâfî yâ Allâh, yâ Muâfî yâ Allâh.\nYâ Bâkî yâ Allâh, yâ Hâdî yâ Allâh.\nYâ Kâdirü yâ Allâh, yâ Sâtirü yâ Allâh.\nYâ Kahhârü yâ Allâh, yâ Cebbârü yâ Allâh.\nYâ Gaffârü yâ Allâh, yâ Fettâhu yâ Allâh.\n\nYâ Rabbe’s-semâvâti ve’l-ard! Yâ ze’l-celâli ve’l-ikrâm! Es’elüke bi-hakkı hâzihi’l-esmâi küllihâ en tusalliye alâ seyyidinâ Muhammedin ve alâ âli Muhammed, verham Muhammeden kemâ salleyte ve sellemte ve bârekte ve rahimte ve terahhamte alâ İbrâhîme ve alâ âli İbrâhîme fi’l-âlemîn. Rabbenâ inneke Hamîdün Mecîd. Bi-rahmetike yâ Erhame’r-râhimîn ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Ey Cemîl (güzellik sahibi) Allah! Ey Karîb (kullarına çok yakın) Allah! Ey Mücîb (dualara icabet eden) Allah! Ey Habîb (sevgili) Allah! ... Ey göklerin ve yerin Rabbi, ey celâl ve ikram sahibi! Bütün bu yüce isimlerinin hakkı için Efendimiz Muhammed’e ve onun âline salât ve rahmet eylemeni diliyoruz. Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "Eller yukarı açık şekilde hürmetle niyaz edilir."
        ),
        NamazTesbihatiStep(
            title = "12. Fetih Sûresi (27-29. Âyetler)",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nلَقَدْ صَدَقَ اللّٰهُ رَسُولَهُ الرُّءْيَا بِالْحَقِّۚ لَتَدْخُلُنَّ الْمَسْجِدَ الْحَرَامَ اِنْ شَٓاءَ اللّٰهُ اٰمِن۪ينَۙ مُحَلِّق۪ينَ رُوُ۫ٔسَكُمْ وَمُقَصِّر۪ينَۙ لاَ تَخَافُونَۜ فَعَلِمَ مَا لَمْ تَعْلَمُوا فَجَعَلَ مِنْ دُونِ ذٰلِكَ فَتْحًا قَر۪يبًا ﴿٢٧﴾\nهُوَ الَّذ۪ٓى اَرْسَلَ رَسُولَهُ بِالْهُدٰى وَد۪ينِ الْحَقِّ لِيُظْهِرَهُ عَلَى الدّ۪ينِ كُلِّه۪ۜ وَكَفٰى بِاللّٰهِ شَه۪يدًۜا ﴿٢٨﴾\nمُحَمَّدٌ رَسُولُ اللّٰهِۜ وَالَّذ۪ينَ مَعَهُٓ اَشِدَّٓاءُ عَلَى الْكُفَّارِ رُحَمَٓاءُ بَيْنَهُمْ تَرٰيهُمْ رُكَّعًا سُجَّدًا يَبْتَغُونَ فَضْلاً مِنَ اللّٰهِ وَرِضْوَانًۘا س۪يمَاهُمْ ف۪ى وُجُوهِهِمْ مِنْ اَثَرِ السُّجُودِۜ ذٰلِكَ مَثَلُهُمْ فِى التَّوْرٰيةِۚۛ وَمَثَلُهُمْ فِى اْلاِنْج۪يلِ۠ۛ كَزَرْعٍ اَخْرَجَ شَطْئَهُ فَاٰزَرَهُ فَاسْتَغْلَظَ فَاسْتَوٰى عَلٰى سُوقِه۪ يُعْجِبُ الزُّرَّاعَ لِيَغ۪يظَ بِهِمُ الْكُفَّارَۜ وَعَدَ اللّٰهُ الَّذ۪ينَ اٰمَنُوا وَعَمِلُوا الصَّالِحَاتِ مِنْهُمْ مَغْفِرَةً وَاَجْرًا عَظ۪يمًا ﴿٢٩﴾",
            textTurkish = "Bismillâhirrahmânirrahîm.\nLekad sadakallâhü rasûlehür-rü’yâ bi’l-hakk, letedhulünne’l-Mescide’l-Harâme in şâallâhü âminîne muhallikîne ruûseküm ve mükassırîne lâ tehâfûn, fe-alime mâ lem ta’lemû feceale min dûni zâlike fethan karîbâ.\nHüvellezî ersele rasûlehû bi’l-hüdâ ve dîni’l-hakkı li-yuzhirahû ale’d-dîni küllih, ve kefâ billâhi şehîdâ.\nMuhammedür-Rasûlüllâh, vellezîne meahû eşiddâü ale’l-küffâri ruhemâü beynehüm terâhüm rukkean sücceden yebtegûne fadlen minallâhi ve rıdvânâ, sîmâhüm fî vücûhihim min eseri’s-sücûd, zâlike meselühüm fi’t-tevrât, ve meselühüm fi’l-incîl, kezer’in ahrace şat’ehû fe-âzerahû festağleza festevâ alâ sûkıhî yu’cibü’z-zürrâa li-yegîza bihimü’l-küffâr, vaadallâhüllezîne âmenû ve amilü’s-sâlihâti minhüm mağfiraten ve ecran azîmâ.",
            meaning = "Andolsun ki Allah, elçisinin rüyasını doğru çıkardı. Allah dilerse güven içinde Mescid-i Haram’a gireceksiniz... Muhammed Allah’ın elçisidir. O’nun yanında bulunanlar kâfirlere karşı çetin, kendi aralarında pek merhametlidirler. Onları rükû ve secdede görürsün... Allah iman edip salih amel işleyenlere mağfiret ve büyük bir mükâfat vaat etmiştir.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "13. Fâtiha Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbi’l-âlemîn. Er-rahmâni’r-rahîm. Mâliki yevmi’d-dîn. İyyâke na’büdü ve iyyâke neste’în. İhdina’s-sırâta’l-müstakîm. Sırâtallezîne en’amte aleyhim gayri’l-magdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Âlemlerin Rabbi Allah'a mahsustur. O, Rahman'dır, Rahim'dir. Hesap ve ceza gününün mâlikidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet; kendilerine nimet verdiklerinin yoluna, gazaba uğrayanların ve sapanlarınkine değil.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "14. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah dualarınızı, zikirlerinizi ve tesbihatınızı kabul eylesin!",
            type = StepType.COMPLETED
        )
    )

    val ikindiSteps = listOf(
        NamazTesbihatiStep(
            title = "1. İstiğfar ve Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme ente’s-selâmü ve minke’s-selâm, tebârakte yâ ze’l-celâli ve’l-ikrâm.",
            meaning = "Allah’ım! Sen Selâm’sın; selâm ve selâmet Senden gelir. Her şeyden üstün ve yücesin ey Celal ve İkram sahibi!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "2. Salâten Tüncînâ",
            textArabic = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Allâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed. Salâten tüncînâ bihâ min cemîı’l-ehvâli ve’l-âfât. Ve takdî lenâ bihâ cemîa’l-hâcât. Ve tutahhirunâ bihâ min cemîı’s-seyyiât. Ve terfeunâ bihâ ındeke a’le’d-derecât. Ve tübelliğunâ bihâ aksa’l-gâyât. Min cemîı’l-hayrâti fi’l-hayâti ve ba’de’l-memât. Âmin yâ mücîbe’d-deavât ve’l-hamdü lillâhi rabbi’l-âlemîn.",
            meaning = "Allah’ım! Efendimiz Muhammed’e ve âline öyle bir salât ve rahmet eyle ki; onunla bizleri bütün korku ve afetlerden kurtar, bütün ihtiyaçlarımızı yerine getir, bizleri günahlardan arındır, katında en yüce derecelere yükselt, hayatta ve ölümden sonra bütün hayırların en son gayesine ulaştır. Âmin, ey dualara icabet eden! Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "\"ve’l-âfât\" derken avuç içleri yere çevrilir, bitince tekrar yukarı çevrilir."
        ),
        NamazTesbihatiStep(
            title = "3. Tesbih ve Tevhid",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Sübhânallâhi vel-hamdü lillâhi ve lâ ilâhe illallâhü vallâhü ekber. Ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm.",
            meaning = "Allah’ı bütün noksan sıfatlardan tenzih ederim. Hamd Allah’adır. Allah’tan başka ilâh yoktur ve Allah en büyüktür. Güç ve kuvvet ancak yüce ve azamet sahibi Allah’tandır.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "4. Âyet-el Kürsî",
            textArabic = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
            textTurkish = "Allâhü lâ ilâhe illâ hüve’l-hayyü’l-kayyûm. Lâ te’huzühû sinetün ve lâ nevm. Lehû mâ fi’s-semâvâti ve mâ fi’l-ard. Men zellezî yeşfeu ındehû illâ bi-iznih. Ya’lemü mâ beyne eydîhim ve mâ halfehüm. Ve lâ yuhîtûne bi-şey’in min ılmihî illâ bimâ şâ’. Vesi’a kürsiyyühü’s-semâvâti ve’l-ard. Ve lâ yeûdühû hıfzuhümâ ve hüve’l-aliyyü’l-azîm.",
            meaning = "Allah, O’ndan başka ilâh yoktur; diridir, her şeyin varlığı Kendisine bağlıdır. Kendisine ne uyuklama ne de uyku gelir. Göklerde ve yerde ne varsa O’nundur. İzni olmaksızın O’nun katında kim şefaat edebilir? Kulların yaptıklarını ve yapacaklarını bilir. O’nun ilminden, Kendisinin dilediği müstesna hiçbir şeyi kavrayamazlar. Kürsüsü gökleri ve yeri kuşatmıştır. Onları koruyup gözetmek O’na asla ağır gelmez. O çok yücedir, çok büyüktür.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "5. Sübhânallah (33 Defa)",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüve’l-aliyyü’l-azîmü zü’l-celâli sübhânallâh.",
            meaning = "O çok yücedir, çok büyüktür; celâl sahibi Allah her türlü noksanlıktan münezzehtir.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "6. Elhamdülillâh (33 Defa)",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübhâne’l-bâkî dâimenil-hamdülillâh.",
            meaning = "Varlığı ebedî olan Allah noksan sıfatlardan uzaktır; daima hamd Allah’a mahsustur.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "7. Allâhuekber (33 Defa)",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbi’l-âlemîne teâlâ şânühü Allâhü ekber.",
            meaning = "Âlemlerin Rabbi olan Allah’ın şânı ne yücedir; Allah en büyüktür.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "8. Tahlil ve Dua",
            textArabic = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
            textTurkish = "Lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, ve hüve hayyün lâ yemût, biyedihi’l-hayru ve hüve alâ külli şey’in kadîr ve ileyhi’l-masîr.",
            meaning = "Allah’tan başka ilâh yoktur; O tektir, ortağı yoktur. Mülk O’nundur, hamd O’na mahsustur. Hayatı veren ve öldüren O’dur. O hiç ölmeyen Hayy’dır. Bütün hayır O’nun elindedir ve O her şeye hakkıyla kādirdir. Dönüş de O’nadır. (Dua vakti: Ellerinizi açarak dua ediniz).",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "9. Kelime-i Tevhid (33 Defa)",
            textArabic = "فَاعْلَمْ أَنَّهُ: لَا إِلٰهَ إِلَّا اللهُ",
            textTurkish = "Fa’lem ennehû: Lâ ilâhe illallâh. (33 defa tekrar edilir. Sonunda: Muhammedü’r-resûlüllâhi sallâllâhü teâlâ aleyhi ve sellem).",
            meaning = "Bil ki: Allah'tan başka ilâh yoktur! (33 defa zikredilir. Sonunda 'Muhammed Allah’ın elçisidir, Allah ona salât ve selâm eylesin' denir).",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "10. Salavat-ı Şerife",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا (٣ defa)\n\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ\n\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا رَسُولَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا حَب۪يبَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ\n\nاَللّٰهُمَّ صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِوَالِدَيْنَا وَ بِجَم۪يعِ الْمُؤْمِن۪ينَ وَ الْمُؤْمِنَاتِ يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nİnnallâhe ve melâiketehû yüsallûne ale’n-nebiy. Yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk.\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, biadedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. (3 defa)\n\nSalli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemîı’l-enbiyâi ve’l-mürselîn ve alâ âli küllin ve sahbi küllin ecmaîn. Âmin ve’l-hamdü lillâhi Rabbi’l-âlemîn.\n\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Resûlallâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Habîballâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ emîne vahyillâh.\n\nAllâhümme salli ve sellim ve bârik alâ seyyidinâ Muhammedin ve alâ âlihî ve ashâbihî biadedi evrâkı’l-eşcâr ve emvâci’l-bihâr ve katarâti’l-emtâr. Vagfir lenâ verhamnâ veltuf binâ ve bivâlideynâ ve bi-cemîı’l-mü’minîne ve’l-mü’minât yâ ilâhenâ bikülli salâtin minhâ. Eşhedü en lâ ilâhe illallâh ve eşhedü enne Muhammeder-resûlüllâh sallâllâhü teâlâ aleyhi ve sellem.",
            meaning = "Şüphesiz Allah ve melekleri Peygamber’e salât ederler. Ey iman edenler, siz de ona tam bir teslimiyetle salât ve selâm getirin! Allah’ım! Efendimiz Muhammed’e ve âline bütün dertler ve devalar adedince salât, bereket ve selâm eyle. Ağaçların yaprakları, denizlerin dalgaları ve yağmurun damlaları adedince salât ve selâm olsun. Milyonlarca salât ve selâm senin üzerine olsun ey Allah’ın Resulü!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "11. Tercüman-ı İsm-i A'zâm Duası",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nسُبْحَانَكَ يَٓا اَللّٰهُ تَعَالَيْتَ يَا رَحْمٰنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا رَح۪يمُ تَعَالَيْتَ يَا كَر۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَم۪يدُ تَعَالَيْتَ يَا حَك۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مَج۪يدُ تَعَالَيْتَ يَا مَلِكُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قُدُّوسُ تَعَالَيْتَ يَا سَلاَمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُؤْمِنُ تَعَالَيْتَ يَا مُهَيْمِنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَز۪يزُ تَعَالَيْتَ يَا جَبَّارُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُتَكَبِّرُ تَعَالَيْتَ يَا خَالِقُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَٓا اَوَّلُ تَعَالَيْتَ يَٓا اٰخِرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا ظَاهِرُ تَعَالَيْتَ يَا بَاطِنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا بَارِىءُ تَعَالَيْتَ يَا مُصَوِّرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا تَوَّابُ تَعَالَيْتَ يَا وَهَّابُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا بَاعِثُ تَعَالَيْتَ يَا وَارِثُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قَد۪يمُ تَعَالَيْتَ يَا مُق۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا فَرْدُ تَعَالَيْتَ يَا وِتْرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا نُورُ تَعَالَيْتَ يَا سَتَّارُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا جَل۪يلُ تَعَالَيْتَ يَا جَم۪يلُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قَاهِرُ تَعَالَيْتَ يَا قَادِرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مَل۪يكُ تَعَالَيْتَ يَا مُقْتَدِرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَل۪يمُ تَعَالَيْتَ يَا عَلاَّمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَظ۪يمُ تَعَالَيْتَ يَا غَفُورُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَل۪يمُ تَعَالَيْتَ يَا وَدُودُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا شَه۪يدُ تَعَالَيْتَ يَا شَاهِدُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا كَب۪يرُ تَعَالَيْتَ يَا مُتَعَالُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا نُورُ تَعَالَيْتَ يَا لَط۪يفُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا سَم۪يعُ تَعَالَيْتَ يَا كَف۪يلُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا قَر۪يبُ تَعَالَيْتَ يَا بَص۪يرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَقُّ تَعَالَيْتَ يَا مُب۪ينُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا رَؤُفُ تَعَالَيْتَ يَا رَح۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا طَاهِرُ تَعَالَيْتَ يَا مُطَهِّرُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُجَمِّلُ تَعَالَيْتَ يَا مُفَضِّلُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا مُظْهِرُ تَعَالَيْتَ يَا مُنْعِمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا دَيَّانُ تَعَالَيْتَ يَا سُلْطَانُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَنَّانُ تَعَالَيْتَ يَا مَنَّانُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَٓا اَحَدُ تَعَالَيْتَ يَا صَمَدُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا حَىُّ تَعَالَيْتَ يَا قَيُّومُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا عَدْلُ تَعَالَيْتَ يَا حَكَمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\nسُبْحَانَكَ يَا فَرْدُ تَعَالَيْتَ يَا قُدُّوسُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ ٭\n\nسُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ جَم۪يعَ الْمُؤْمِن۪ينَ مِنَ النَّارِ ٭\nوَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ ٭\nبِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nSübhâneke yâ Allâh teâleyte yâ Rahmân ecirnâ mine’n-nâr bi-afvike yâ Rahmân.\nSübhâneke yâ Rahîm teâleyte yâ Kerîm ecirnâ mine’n-nâr bi-afvike yâ Rahmân...\n(Dua devam eder...)\nSübhâneke âhiyyen şerâhiyyen teâleyte lâ ilâhe illâ ente ecirnâ ve ecir vâlideynâ ve ihvânenâ ve ehavâtinâ ve cemîa’l-mü’minîne mine’n-nâr.\nVe min külli nâr, vahfeznâ min şerri’n-nefsi ve’ş-şeytân ve min şerri’l-cinni ve’l-insân ve min şerri’l-bid’ati ve’d-dalâlâti ve’l-ilhâdi ve’t-tuğyân.\nBi-afvike yâ Mücîr, bi-fadlike yâ Gaffâr, bi-rahmetike yâ Erhame’r-râhimîn. Allâhümme edhilne’l-Cennete mea’l-ebrâr bi-şefâati nebiyyike’l-muhtâr. Âmîn, ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Sen her noksandan münezzehsin ey Allah! Sen her şeyden yücesin ey Rahmân! Affınla bizi cehennem ateşinden koru ey merhameti sonsuz olan! (Allah’ın güzel isimleri ile cehennem ateşinden, nefsin, şeytanın, cinnin ve insanların şerrinden, bidat ve sapkınlıklardan Allah’a sığınma ve cenneti talep etme duası).",
            type = StepType.PRAYER,
            handGesture = "\"Sübhâneke âhiyyen...\" kısmında eller açık; \"Ve min külli nâr...\" kısmında avuçlar aşağı çevrilir; \"Bi-afvike...\" kısmında tekrar yukarı açılır."
        ),
        NamazTesbihatiStep(
            title = "12. Nebe' Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nعَمَّ يَتَسَٓاءَلُونَۚ ﴿١﴾ عَنِ النَّبَأِ الْعَظ۪يمِۙ ﴿٢﴾ اَلَّذ۪ى هُمْ ف۪يهِ مُخْتَلِفُونَۜ ﴿٣﴾ كَلَّا سَيَعْلَمُونَۙ ﴿٤﴾ ثُمَّ كَلَّا سَيَعْلَمُونَ ﴿٥﴾\nاَلَمْ نَجْعَلِ اْلاَرْضَ مِهَادًۙا ﴿٦﴾ وَالْجِبَالَ اَوْتَادًۖا ﴿٧﴾ وَخَلَقْنَاكُمْ اَزْوَاجًۙا ﴿٨﴾ وَجَعَلْنَا نَوْمَكُمْ سُبَاتًۙا ﴿٩﴾ وَجَعَلْنَا الَّيْلَ لِبَاسًۙا ﴿١٠﴾ وَجَعَلْنَا النَّهَارَ مَعَاشًۖا ﴿١١﴾ وَبَنَيْنَا فَوْقَكُمْ سَبْعًا شِدَادًۙا ﴿١٢﴾ وَجَعَلْنَا سِرَاجًا وَهَّاجًۖا ﴿١٣﴾ وَاَنْزَلْنَا مِنَ الْمُعْصِرَاتِ مَٓاءً ثَجَّاجًۙا ﴿١٤﴾ لِنُخْرِجَ بِه۪ حَبًّا وَنَبَاتًۙا ﴿١٥﴾ وَجَنَّاتٍ اَلْفَافًۜا ﴿١٦﴾\nاِنَّ يَوْمَ الْفَصْلِ كَانَ م۪يقَاتًۙا ﴿١٧﴾ يَوْمَ يُنْفَخُ فِى الصُّورِ فَتَاْتُونَ اَفْوَاجًۙا ﴿١٨﴾ وَفُتِحَتِ السَّمَٓاءُ فَكَانَتْ اَبْوَابًۙا ﴿١٩﴾ وَسُيِّرَتِ الْجِبَالُ فَكَانَتْ سَرَابًۜا ﴿٢٠﴾\nاِنَّ جَهَنَّمَ كَانَتْ مِرْصَادًۙا ﴿٢١﴾ لِلطَّاغ۪ينَ مَاٰبًۙا ﴿٢٢﴾ لاَبِث۪ينَ ف۪يهَٓا اَحْقَابًۚا ﴿٢٣﴾ لاَيَذُوقُونَ ف۪يهَا بَرْدًا وَلاَ شَرَابًۙا ﴿٢٤﴾ اِلاَّ حَم۪يمًا وَغَسَّاقًۙا ﴿٢٥﴾ جَزَٓاءً وِفَاقًا ﴿٢٦﴾\nاِنَّ لِلْمُتَّق۪ينَ مَفَازًۙا ﴿٣١﴾ حَدَٓائِقَ وَاَعْنَابًۙا ﴿٣٢﴾ وَكَوَاعِبَ اَتْرَابًۙا ﴿٣٣﴾ وَكَاْسًا دِهَاقًۜا ﴿٣٤﴾ لاَيَسْمَعُونَ ف۪يهَا لَغْوًا وَلاَ كِذَّابًۚا ﴿٣٥﴾ جَزَٓاءً مِنْ رَبِّكَ عَطَٓاءً حِسَابًۙا ﴿٣٦﴾\nرَبِّ السَّمٰوَاتِ وَاْلاَرْضِ وَمَا بَيْنَهُمَاۙ الرَّحْمٰنِ لاَيَمْلِكُونَ مِنْهُ خِطَابًۙا ﴿٣٧﴾ يَوْمَ يَقُومُ الرُّوحُ وَالْمَلٰٓئِكَةُ صَفًّاۜ لاَيَتَكَلَّمُونَ اِلاَّ مَنْ اَذِنَ لَهُ الرَّحْمٰنُ وَقَالَ صَوَابًا ﴿٣٨﴾ ذٰلِكَ الْيَوْمُ الْحَقُّۚ فَمَنْ شَٓاءَ اتَّخَذَ اِلٰى رَبِّه۪ مَاٰبًا ﴿٣٩﴾ اِنَّٓا اَنْذَرْنَاكُمْ عَذَابًا قَر۪يبًۚا يَوْمَ يَنْظُرُ الْمَرْءُ مَا قَدَّمَتْ يَدَاهُ وَيَقُولُ الْكَافِرُ يَالَيْتَن۪ى كُنْتُ تُرَابًا ﴿٤٠﴾",
            textTurkish = "Bismillâhirrahmânirrahîm.\nAmme yetesâelûn. Anin-nebeil-azîm. Ellezî hüm fîhi muhtelifûn. Kellâ seya’lemûn. Sümme kellâ seya’lemûn.\nElem nec’ali’l-arda mihâdâ. Vel-cibâle evtâdâ. Ve halaknâküm ezvâcâ. Ve cealnâ nevmeküm sübâtâ. Ve cealne’l-leyle libâsâ. Ve cealne’n-nehâre meâşâ. Ve beneynâ fevkaküm seb’an şidâdâ. Ve cealnâ sirâcen vehhâcâ. Ve enzelnâ mine’l-mu’sırâti mâen seccâcâ. Li-nuhrica bihî habben ve nebâtâ. Ve cennâtin elfâfâ.\nİnne yevme’l-fasli kâne mîkâtâ. Yevme yünfehu fis-sûri fe-te’tûne efvâcâ... İnne lil-müttekîne mefâzâ. Hadâika ve a’nâbâ... İnne mâ tüadûne leât. İnne zâlike fî kitâb...",
            meaning = "Birbirlerine neyi soruyorlar? Hakkında ayrılığa düştükleri o büyük haberi mi? Hayır, yakında bilecekler! Biz yeri bir beşik, dağları birer direk kılmadık mı? Şüphesiz takva sahipleri için büyük bir kurtuluş, bahçeler ve üzüm bağları vardır...",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "13. Fâtiha Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbi’l-âlemîn. Er-rahmâni’r-rahîm. Mâliki yevmi’d-dîn. İyyâke na’büdü ve iyyâke neste’în. İhdina’s-sırâta’l-müstakîm. Sırâtallezîne en’amte aleyhim gayri’l-magdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Âlemlerin Rabbi Allah'a mahsustur. O, Rahman'dır, Rahim'dir. Hesap ve ceza gününün mâlikidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet; kendilerine nimet verdiklerinin yoluna, gazaba uğrayanların ve sapanlarınkine değil.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "14. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah dualarınızı, zikirlerinizi ve tesbihatınızı kabul eylesin!",
            type = StepType.COMPLETED
        )
    )

    val aksamSteps = listOf(
        NamazTesbihatiStep(
            title = "1. İstiğfar ve Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme ente’s-selâmü ve minke’s-selâm, tebârakte yâ ze’l-celâli ve’l-ikrâm.",
            meaning = "Allah’ım! Sen Selâm’sın; selâm ve selâmet Senden gelir. Her şeyden üstün ve yücesin ey Celal ve İkram sahibi!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "2. Salâten Tüncînâ",
            textArabic = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Allâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed. Salâten tüncînâ bihâ min cemîı’l-ehvâli ve’l-âfât. Ve takdî lenâ bihâ cemîa’l-hâcât. Ve tutahhirunâ bihâ min cemîı’s-seyyiât. Ve terfeunâ bihâ ındeke a’le’d-derecât. Ve tübelliğunâ bihâ aksa’l-gâyât. Min cemîı’l-hayrâti fi’l-hayâti ve ba’de’l-memât. Âmin yâ mücîbe’d-deavât ve’l-hamdü lillâhi rabbi’l-âlemîn.",
            meaning = "Allah’ım! Efendimiz Muhammed’e ve âline öyle bir salât ve rahmet eyle ki; onunla bizleri bütün korku ve afetlerden kurtar, bütün ihtiyaçlarımızı yerine getir, bizleri günahlardan arındır, katında en yüce derecelere yükselt, hayatta ve ölümden sonra bütün hayırların en son gayesine ulaştır. Âmin, ey dualara icabet eden! Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "\"ve’l-âfât\" derken avuç içleri yere çevrilir, bitince tekrar yukarı çevrilir."
        ),
        NamazTesbihatiStep(
            title = "3. Tevhid (10 Defa)",
            textArabic = "اٰمَنَّا بِاَنَّهُ:\n\nلآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ",
            textTurkish = "Âmennâ bi-ennehû: Lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, biyedihi’l-hayr ve hüve alâ külli şey’in kadîr. (10. tekrarın sonunda 've hüve hayyün lâ yemût... ve ileyhi’l-masîr' eklenir).",
            meaning = "İman eder ve tasdik ederiz ki: Allah'tan başka ilâh yoktur; O birdir, ortağı yoktur. Mülk O'nundur, hamd O'na mahsustur. Hayatı veren ve öldüren O'dur, bütün hayır O'nun elindedir ve O her şeye hakkıyla kadirdir. Dönüş de O'nadır.",
            type = StepType.COUNTER,
            targetCount = 10
        ),
        NamazTesbihatiStep(
            title = "4. İstiâze (7 Defa)",
            textArabic = "اَللّٰهُمَّ اَجِرْنَا مِنَ النَّارِ",
            textTurkish = "Allâhümme ecirnâ mine’n-nâr.",
            meaning = "Allah’ım! Bizi cehennem ateşinden kurtar.",
            type = StepType.COUNTER,
            targetCount = 7,
            handGesture = "Avuç içleri aşağıya (yere bakacak şekilde) tutulur."
        ),
        NamazTesbihatiStep(
            title = "5. Ateşten Korunma Duası",
            textArabic = "اَللّٰهُمَّ اَجِرْنَا مِنْ كُلِّ نَارٍ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الدّ۪ينِيَّةِ وَ الدُّنْيَوِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ اٰخِرِ الزَّمَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْمَس۪يحِ الدَّجَّالِ وَ السُّفْيَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الضَّلاَلاَتِ وَ الْبِدْعِيَّاتِ وَ الْبَلِيَّاتِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النَّفْسِ اْلاَمَّارَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شُرُورِ النُّفُوسِ اْلاَمَّارَاتِ الْفِرْعَوْنِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ بَلآَءِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ يَوْمِ الْقِيٰمَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ جَهَنَّمَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ نَارِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ وَ النِّرَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الرِّيَاءِ وَ السُّمْعَةِ وَ الْعُجُبِ وَ الْفَخْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ تَجَاوُزِ الْمُلْحِد۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ الْمُنَافِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْفَاسِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ ف۪ى خِدْمَةِ الْقُرْاٰنِ وَ اْلا۪يمَانِ وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا مِنَ النَّارِ",
            textTurkish = "Allâhümme ecirnâ min külli nâr. Allâhümme ecirnâ min fitneti’d-dîniyyeti ve’d-dünyeviyyeh. Allâhümme ecirnâ min fitneti âhiri’z-zamân. Allâhümme ecirnâ min fitneti Mesîhi’d-Deccâli ve’s-Süfyân. Allâhümme ecirnâ mine’d-dalâlâti ve’l-bid’ıyyâti ve’l-beliyyât. Allâhümme ecirnâ min şerri’n-nefsi’l-emmâreh. Allâhümme ecirnâ min şürûri’n-nüfûsi’l-emmârâti’l-fir’avniyyeh. Allâhümme ecirnâ min şerri’n-nisâ. Allâhümme ecirnâ min belâi’n-nisâ. Allâhümme ecirnâ min fitneti’n-nisâ. Allâhümme ecirnâ min azâbi’l-kabr. Allâhümme ecirnâ min azâbi yevmi’l-kıyâmeh. Allâhümme ecirnâ min azâbi cehennem. Allâhümme ecirnâ min azâbi kahrik. Allâhümme ecirnâ min nâri kahrik. Allâhümme ecirnâ min azâbi’l-kabri ve’n-nîrân. Allâhümme ecirnâ mine’r-riyâi ve’s-süm’ati ve’l-ucubi ve’l-fahr. Allâhümme ecirnâ min tecâvüzi’l-mülhidîn. Allâhümme ecirnâ min şerri’l-münâfikîn. Allâhümme ecirnâ min fitneti’l-fâsikîn. Allâhümme ecirnâ ve ecir vâlideynâ ve talebete Resâili’n-Nûri’s-sâdikîne fî hidmeti’l-Kur’âni ve’l-îmân. Ve ahbâbene’l-mü’minîne’l-muhlisîne ve akribâenâ ve ecdâdenâ mine’n-nâr.",
            meaning = "Allah’ım! Bizi her çeşit ateşten kurtar. Dini ve dünyevi fitnelerden muhafaza eyle. Ahir zaman fitnesinden koru. Deccal ve Süfyan fitnesinden, sapkınlıklardan, bidatlardan ve belalardan halas eyle. Kötülüğü emreden nefsin şerrinden, kabir, kıyamet ve cehennem azabından kurtar. Riya, şöhret arzusu ve kibirden muhafaza eyle. Bizi, anne-babamızı ve bütün mümin kardeşlerimizi cehennem ateşinden halas eyle.",
            type = StepType.PRAYER,
            handGesture = "Avuç içleri aşağıya (yere bakacak şekilde) tutulmaya devam edilir."
        ),
        NamazTesbihatiStep(
            title = "6. Cennet Duası",
            textArabic = "بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا وَ اَدْخِلْ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ ف۪ى خِدْمَةِ اْلا۪يمَانِ وَ الْقُرْاٰنِ اَلْجَنَّةَ مَعَ اْلاَبْرَارِ صَلِّ عَلٰى نَبِيِّكَ الْمُخْتَارِ وَ اٰلِه۪ اْلاَطْهَارِ وَ اَصْحَابِهِ اْلاَخْيَارِ وَ سَلِّمْ مَادَامَ الَّيْلُ وَ النَّهَارُ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bi-afvike yâ Mücîr, bi-fadlike yâ Gaffâr. Allâhümme edhilne’l-Cennete mea’l-ebrâr. Allâhümme edhilne’l-Cennete mea’l-ebrâr. Allâhümme edhilnâ ve edhil vâlideynâ ve ihvânenâ ve ehavâtinâ ve akribâenâ ve ecdâdenâ ve ahbâbene’l-mü’minîne’l-muhlisîne fî hidmeti’l-îmâni ve’l-Kur’ân. El-Cennete mea’l-ebrâr bi-şefâati nebiyyike’l-muhtâr ve âlihi’l-ethâr ve ashâbihi’l-ahyâr ve sellim mâ dâme’l-leylü ve’n-nehâr. Âmîn ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Affınla kabul buyur ey kurtaran Mücîr! Fazlınla bağışla ey günahları örten Gaffâr! Allah’ım! Bizleri ebrâr olan salih kullarınla beraber Cennet’e idhal eyle. Bizi, ana-babamızı, iman ve Kur'an yolundaki kardeşlerimizi, Peygamberimiz’in şefaati ve ashabının hürmetine Cennet’e nail eyle. Gece ve gündüz devam ettikçe onlara selâm olsun. Âmin, Âlemlerin Rabbi olan Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "Avuç içleri tekrar yukarı çevrilerek semaya açılır."
        ),
        NamazTesbihatiStep(
            title = "7. Tesbih ve Tevhid",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Sübhânallâhi vel-hamdü lillâhi ve lâ ilâhe illallâhü vallâhü ekber. Ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm.",
            meaning = "Allah’ı bütün noksan sıfatlardan tenzih ederim. Hamd Allah’adır. Allah’tan başka ilâh yoktur ve Allah en büyüktür. Güç ve kuvvet ancak yüce ve azamet sahibi Allah’tandır.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "8. Âyet-el Kürsî",
            textArabic = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
            textTurkish = "Allâhü lâ ilâhe illâ hüve’l-hayyü’l-kayyûm. Lâ te’huzühû sinetün ve lâ nevm. Lehû mâ fi’s-semâvâti ve mâ fi’l-ard. Men zellezî yeşfeu ındehû illâ bi-iznih. Ya’lemü mâ beyne eydîhim ve mâ halfehüm. Ve lâ yuhîtûne bi-şey’in min ılmihî illâ bimâ şâ’. Vesi’a kürsiyyühü’s-semâvâti ve’l-ard. Ve lâ yeûdühû hıfzuhümâ ve hüve’l-aliyyü’l-azîm.",
            meaning = "Allah, O’ndan başka ilâh yoktur; diridir, her şeyin varlığı Kendisine bağlıdır. Kendisine ne uyuklama ne de uyku gelir. Göklerde ve yerde ne varsa O’nundur. İzni olmaksızın O’nun katında kim şefaat edebilir? Kulların yaptıklarını ve yapacaklarını bilir. O’nun ilminden, Kendisinin dilediği müstesna hiçbir şeyi kavrayamazlar. Kürsüsü gökleri ve yeri kuşatmıştır. Onları koruyup gözetmek O’na asla ağır gelmez. O çok yücedir, çok büyüktür.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "9. Sübhânallah (33 Defa)",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüve’l-aliyyü’l-azîmü zü’l-celâli sübhânallâh.",
            meaning = "O çok yücedir, çok büyüktür; celâl sahibi Allah her türlü noksanlıktan münezzehtir.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "10. Elhamdülillâh (33 Defa)",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübhâne’l-bâkî dâimenil-hamdülillâh.",
            meaning = "Varlığı ebedî olan Allah noksan sıfatlardan uzaktır; daima hamd Allah’a mahsustur.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "11. Allâhuekber (33 Defa)",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbi’l-âlemîne teâlâ şânühü Allâhü ekber.",
            meaning = "Âlemlerin Rabbi olan Allah’ın şânı ne yücedir; Allah en büyüktür.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "12. Tahlil ve Dua",
            textArabic = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
            textTurkish = "Lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, ve hüve hayyün lâ yemût, biyedihi’l-hayru ve hüve alâ külli şey’in kadîr ve ileyhi’l-masîr.",
            meaning = "Allah’tan başka ilâh yoktur; O tektir, ortağı yoktur. Mülk O’nundur, hamd O’na mahsustur. Hayatı veren ve öldüren O’dur. O hiç ölmeyen Hayy’dır. Bütün hayır O’nun elindedir ve O her şeye hakkıyla kādirdir. Dönüş de O’nadır. (Dua vakti: Ellerinizi açarak dua ediniz).",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "13. Kelime-i Tevhid (33 Defa)",
            textArabic = "فَاعْلَمْ أَنَّهُ: لَا إِلٰهَ إِلَّا اللهُ",
            textTurkish = "Fa’lem ennehû: Lâ ilâhe illallâh. (33 defa tekrar edilir. Sonunda: Muhammedü’r-resûlüllâhi sallâllâhü teâlâ aleyhi ve sellem).",
            meaning = "Bil ki: Allah'tan başka ilâh yoktur! (33 defa zikredilir. Sonunda 'Muhammed Allah’ın elçisidir, Allah ona salât ve selâm eylesin' denir).",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "14. Salavat-ı Şerife",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا (٣ defa)\n\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ\n\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا رَسُولَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا حَب۪يبَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ\n\nاَللّٰهُمَّ صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِوَالِدَيْنَا وَ بِجَم۪يعِ الْمُؤْمِن۪ينَ وَ الْمُؤْمِنَاتِ يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nİnnallâhe ve melâiketehû yüsallûne ale’n-nebiy. Yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk.\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, biadedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. (3 defa)\n\nSalli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemîı’l-enbiyâi ve’l-mürselîn ve alâ âli küllin ve sahbi küllin ecmaîn. Âmin ve’l-hamdü lillâhi Rabbi’l-âlemîn.\n\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Resûlallâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Habîballâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ emîne vahyillâh.\n\nAllâhümme salli ve sellim ve bârik alâ seyyidinâ Muhammedin ve alâ âlihî ve ashâbihî biadedi evrâkı’l-eşcâr ve emvâci’l-bihâr ve katarâti’l-emtâr. Vagfir lenâ verhamnâ veltuf binâ ve bivâlideynâ ve bi-cemîı’l-mü’minîne ve’l-mü’minât yâ ilâhenâ bikülli salâtin minhâ. Eşhedü en lâ ilâhe illallâh ve eşhedü enne Muhammeder-resûlüllâh sallâllâhü teâlâ aleyhi ve sellem.",
            meaning = "Şüphesiz Allah ve melekleri Peygamber’e salât ederler. Ey iman edenler, siz de ona tam bir teslimiyetle salât ve selâm getirin! Allah’ım! Efendimiz Muhammed’e ve âline bütün dertler ve devalar adedince salât, bereket ve selâm eyle. Ağaçların yaprakları, denizlerin dalgaları ve yağmurun damlaları adedince salât ve selâm olsun. Milyonlarca salât ve selâm senin üzerine olsun ey Allah’ın Resulü!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "15. İsm-i A'zâm Duası",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nيَا جَم۪يلُ يَٓا اَللّٰهُ ٭ يَا قَر۪يبُ يَٓا اَللّٰهُ\nيَا مُج۪يبُ يَٓا اَللّٰهُ ٭ يَا حَب۪يبُ يَٓا اَللّٰهُ\nيَا رَؤُفُ يَٓا اَللّٰهُ ٭ يَا عَطُوفُ يَٓا اَللّٰهُ\nيَا مَعْرُوفُ يَٓا اَللّٰهُ ٭ يَا لَط۪يفُ يَٓا اَللّٰهُ\nيَا عَظ۪يمُ يَٓا اَللّٰهُ ٭ يَا حَنَّانُ يَٓا اَللّٰهُ\nيَا مَنَّانُ يَٓا اَللّٰهُ ٭ يَا دَيَّانُ يَٓا اَللّٰهُ\nيَا سُبْحَانُ يَٓا اَللّٰهُ ٭ يَٓا اَمَانُ يَٓا اَللّٰهُ\nيَا بُرْهَانُ يَٓا اَللّٰهُ ٭ يَا سُلْطَانُ يَٓا اَللّٰهُ\nيَا مُسْتَعَانُ يَٓا اَللّٰهُ ٭ يَا مُحْسِنُ يَٓا اَللّٰهُ\nيَا مُتَعَالُ يَٓا اَللّٰهُ ٭ يَا رَحْمٰنُ يَٓا اَللّٰهُ\nيَا رَح۪يمُ يَٓا اَللّٰهُ ٭ يَا كَر۪يمُ يَٓا اَللّٰهُ\nيَا مَج۪يدُ يَٓا اَللّٰهُ ٭ يَا فَرْدُ يَٓا اَللّٰهُ\nيَا وِتْرُ يَٓا اَللّٰهُ ٭ يَٓا اَحَدُ يَٓا اَللّٰهُ\nيَا صَمَدُ يَٓا اَللّٰهُ ٭ يَا مَحْمُودُ يَٓا اَللّٰهُ\nيَا صَادِقَ الْوَعْدِ يَٓا اَللّٰهُ ٭ يَا عَلِىُّ يَٓا اَللّٰهُ\nيَا غَنِىُّ يَٓا اَللّٰهُ ٭ يَا شَاف۪ى يَٓا اَللّٰهُ\nيَا كَاف۪ى يَٓا اَللّٰهُ ٭ يَا مُعَاف۪ى يَٓا اَللّٰهُ\nيَا بَاق۪ى يَٓا اَللّٰهُ ٭ يَا هَاد۪ى يَٓا اَللّٰهُ\nيَا قَادِرُ يَٓا اَللّٰهُ ٭ يَا سَاتِرُ يَٓا اَللّٰهُ\nيَا قَهَّارُ يَٓا اَللّٰهُ ٭ يَا جَبَّارُ يَٓا اَللّٰهُ\nيَا غَفَّارُ يَٓا اَللّٰهُ ٭ يَا فَتَّاحُ يَٓا اَللّٰهُ\n\nيَا رَبَّ السَّمٰوَاتِ وَ اْلاَرْضِ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ اَسْئَلُكَ بِحَقِّ هٰذِهِ اْلاَسْمَٓاءِ كُلِّهَٓا اَنْ تُصَلِّىَ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلٰٓى اٰلِ مُحَمَّدٍ وَ ارْحَمْ مُحَمَّدًا كَمَا صَلَّيْتَ وَ سَلَّمْتَ وَ بَارَكْتَ وَ رَحِمْتَ وَ تَرَحَّمْتَ عَلٰٓى اِبرَاه۪يمَ وَ عَلٰٓى اٰلِ اِبْرَاه۪يمَ فِى الْعَالَم۪ينَ رَبَّنَٓا اِنَّكَ حَم۪يدٌ مَج۪يدٌ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ وَ الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nYâ Cemîlü yâ Allâh, yâ Karîbü yâ Allâh.\nYâ Mücîbü yâ Allâh, yâ Habîbü yâ Allâh.\nYâ Raûfü yâ Allâh, yâ Atûfü yâ Allâh.\nYâ Ma’rûfü yâ Allâh, yâ Latîfü yâ Allâh.\nYâ Azîmü yâ Allâh, yâ Hannânü yâ Allâh.\nYâ Mennânü yâ Allâh, yâ Deyyânü yâ Allâh.\nYâ Sübhânü yâ Allâh, yâ Emânü yâ Allâh.\nYâ Burhânü yâ Allâh, yâ Sultânü yâ Allâh.\nYâ Müsteânü yâ Allâh, yâ Muhsinü yâ Allâh.\nYâ Müteâlü yâ Allâh, yâ Rahmânü yâ Allâh.\nYâ Rahîmü yâ Allâh, yâ Kerîmü yâ Allâh.\nYâ Mecîdü yâ Allâh, yâ Ferdü yâ Allâh.\nYâ Vitrü yâ Allâh, yâ Ehadü yâ Allâh.\nYâ Samedü yâ Allâh, yâ Mahmûdü yâ Allâh.\nYâ Sâdıka’l-va’di yâ Allâh, yâ Aliyyü yâ Allâh.\nYâ Ganiyyü yâ Allâh, yâ Şâfî yâ Allâh.\nYâ Kâfî yâ Allâh, yâ Muâfî yâ Allâh.\nYâ Bâkî yâ Allâh, yâ Hâdî yâ Allâh.\nYâ Kâdirü yâ Allâh, yâ Sâtirü yâ Allâh.\nYâ Kahhârü yâ Allâh, yâ Cebbârü yâ Allâh.\nYâ Gaffârü yâ Allâh, yâ Fettâhu yâ Allâh.\n\nYâ Rabbe’s-semâvâti ve’l-ard! Yâ ze’l-celâli ve’l-ikrâm! Es’elüke bi-hakkı hâzihi’l-esmâi küllihâ en tusalliye alâ seyyidinâ Muhammedin ve alâ âli Muhammed, verham Muhammeden kemâ salleyte ve sellemte ve bârekte ve rahimte ve terahhamte alâ İbrâhîme ve alâ âli İbrâhîme fi’l-âlemîn. Rabbenâ inneke Hamîdün Mecîd. Bi-rahmetike yâ Erhame’r-râhimîn ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Ey Cemîl (güzellik sahibi) Allah! Ey Karîb (kullarına çok yakın) Allah! Ey Mücîb (dualara icabet eden) Allah! Ey Habîb (sevgili) Allah! ... Ey göklerin ve yerin Rabbi, ey celâl ve ikram sahibi! Bütün bu yüce isimlerinin hakkı için Efendimiz Muhammed’e ve onun âline salât ve rahmet eylemeni diliyoruz. Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "Eller yukarı açık şekilde hürmetle niyaz edilir."
        ),
        NamazTesbihatiStep(
            title = "16. Haşr Sûresi (20-24. Âyetler)",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nلاَ يَسْتَو۪ٓى اَصْحَابُ النَّارِ وَاَصْحَابُ الْجَنَّةِۜ اَصْحَابُ الْجَنَّةِ هُمُ الْفَٓائِزُونَ ﴿٢٠﴾\nلَوْ اَنْزَلْنَا هٰذَا الْقُرْاٰنَ عَلٰى جَبَلٍ لَرَاَيْتَهُ خَاشِعًا مُتَصَدِّعًا مِنْ خَشْيَةِ اللّٰهِۜ وَتِلْكَ اْلاَمْثَالُ نَضْرِبُهَا لِلنَّاسِ لَعَلَّهُمْ يَتَفَكَّرُونَ ﴿٢١﴾\nهُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ عَالِمُ الْغَيْبِ وَالشَّهَادَةِۚ هُوَ الرَّحْمٰنُ الرَّح۪يمُ ﴿٢٢﴾\nهُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ اَلْمَلِكُ الْقُدُّوسُ السَّلاَمُ الْمُؤْمِنُ الْمُهَيْمِنُ الْعَز۪يزُ الْجَبَّارُ الْمُتَكَبِّرُۜ سُبْحَانَ اللّٰهِ عَمَّا يُشْرِكُونَ ﴿٢٣﴾\nهُوَ اللّٰهُ الْخَالِقُ الْبَارِىُٔ الْمُصَوِّرُ لَهُ اْلاَسْمَٓاءُ الْحُسْنٰىۜ يُسَبِّحُ لَهُ مَا فِى السَّمٰوَاتِ وَاْلاَرْضِۚ وَهُوَ الْعَز۪يزُ الْحَك۪يمُ ﴿٢٤﴾",
            textTurkish = "Bismillâhirrahmânirrahîm.\nLâ yestevî ashâbü’n-nâri ve ashâbü’l-Cenneh, ashâbü’l-Cenneti hümü’l-fâizûn.\nLev enzelnâ hâze’l-Kur’âne alâ cebelin leraeytehû hâşi’an mütasaddian min haşyetillâh, ve tilke’l-emsâlü nadribühâ lin-nâsi leallehüm yetefekkerûn.\nHüvallâhüllezî lâ ilâhe illâ hû, âlimü’l-gaybi ve’ş-şehâdeh, hüve’r-Rahmânü’r-Rahîm.\nHüvallâhüllezî lâ ilâhe illâ hû, el-Melikü’l-Kuddûsü’s-Selâmü’l-Mü’minü’l-Müheyminü’l-Azîzü’l-Cebbârü’l-Mütekebbir, sübhânallâhi ammâ yüşrikûn.\nHüvallâhü’l-Hâliku’l-Bâriü’l-Musavviru lehü’l-Esmâü’l-Hüsnâ, yüsebbihu lehû mâ fi’s-semâvâti ve’l-ard, ve hüve’l-Azîzü’l-Hakîm.",
            meaning = "Cehennemliklerle cennetlikler bir olmaz; kurtuluşa erenler cennetliklerdir. Eğer biz bu Kur’an’ı bir dağa indirseydik, Allah korkusundan boyun eğip paramparça olduğunu görürdün... O Allah ki, O'ndan başka ilâh yoktur. Gaybı da görünen âlemi de bilendir; O Rahmân’dır, Rahîm’dir. O Melik, Kuddûs, Selâm, Mü’min, Müheymin, Azîz, Cebbâr, Mütekebbir’dir. En güzel isimler O’nundur.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "17. Fâtiha Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbi’l-âlemîn. Er-rahmâni’r-rahîm. Mâliki yevmi’d-dîn. İyyâke na’büdü ve iyyâke neste’în. İhdina’s-sırâta’l-müstakîm. Sırâtallezîne en’amte aleyhim gayri’l-magdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Âlemlerin Rabbi Allah'a mahsustur. O, Rahman'dır, Rahim'dir. Hesap ve ceza gününün mâlikidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet; kendilerine nimet verdiklerinin yoluna, gazaba uğrayanların ve sapanlarınkine değil.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "18. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah dualarınızı, zikirlerinizi ve tesbihatınızı kabul eylesin!",
            type = StepType.COMPLETED
        )
    )

    val yatsiSteps = listOf(
        NamazTesbihatiStep(
            title = "1. İstiğfar ve Selam",
            textArabic = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            textTurkish = "Allâhümme ente’s-selâmü ve minke’s-selâm, tebârakte yâ ze’l-celâli ve’l-ikrâm.",
            meaning = "Allah’ım! Sen Selâm’sın; selâm ve selâmet Senden gelir. Her şeyden üstün ve yücesin ey Celal ve İkram sahibi!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "2. Salâten Tüncînâ",
            textArabic = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Allâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed. Salâten tüncînâ bihâ min cemîı’l-ehvâli ve’l-âfât. Ve takdî lenâ bihâ cemîa’l-hâcât. Ve tutahhirunâ bihâ min cemîı’s-seyyiât. Ve terfeunâ bihâ ındeke a’le’d-derecât. Ve tübelliğunâ bihâ aksa’l-gâyât. Min cemîı’l-hayrâti fi’l-hayâti ve ba’de’l-memât. Âmin yâ mücîbe’d-deavât ve’l-hamdü lillâhi rabbi’l-âlemîn.",
            meaning = "Allah’ım! Efendimiz Muhammed’e ve âline öyle bir salât ve rahmet eyle ki; onunla bizleri bütün korku ve afetlerden kurtar, bütün ihtiyaçlarımızı yerine getir, bizleri günahlardan arındır, katında en yüce derecelere yükselt, hayatta ve ölümden sonra bütün hayırların en son gayesine ulaştır. Âmin, ey dualara icabet eden! Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "\"ve’l-âfât\" derken avuç içleri yere çevrilir, bitince tekrar yukarı çevrilir."
        ),
        NamazTesbihatiStep(
            title = "3. Tesbih ve Tevhid",
            textArabic = "سُبْحَانَ اللهِ وَالْحَمْدُ للهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
            textTurkish = "Sübhânallâhi vel-hamdü lillâhi ve lâ ilâhe illallâhü vallâhü ekber. Ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm.",
            meaning = "Allah’ı bütün noksan sıfatlardan tenzih ederim. Hamd Allah’adır. Allah’tan başka ilâh yoktur ve Allah en büyüktür. Güç ve kuvvet ancak yüce ve azamet sahibi Allah’tandır.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "4. Âyet-el Kürsî",
            textArabic = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
            textTurkish = "Allâhü lâ ilâhe illâ hüve’l-hayyü’l-kayyûm. Lâ te’huzühû sinetün ve lâ nevm. Lehû mâ fi’s-semâvâti ve mâ fi’l-ard. Men zellezî yeşfeu ındehû illâ bi-iznih. Ya’lemü mâ beyne eydîhim ve mâ halfehüm. Ve lâ yuhîtûne bi-şey’in min ılmihî illâ bimâ şâ’. Vesi’a kürsiyyühü’s-semâvâti ve’l-ard. Ve lâ yeûdühû hıfzuhümâ ve hüve’l-aliyyü’l-azîm.",
            meaning = "Allah, O’ndan başka ilâh yoktur; diridir, her şeyin varlığı Kendisine bağlıdır. Kendisine ne uyuklama ne de uyku gelir. Göklerde ve yerde ne varsa O’nundur. İzni olmaksızın O’nun katında kim şefaat edebilir? Kulların yaptıklarını ve yapacaklarını bilir. O’nun ilminden, Kendisinin dilediği müstesna hiçbir şeyi kavrayamazlar. Kürsüsü gökleri ve yeri kuşatmıştır. Onları koruyup gözetmek O’na asla ağır gelmez. O çok yücedir, çok büyüktür.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "5. Sübhânallah (33 Defa)",
            textArabic = "وَهُوَ الْعَلِيُّ الْعَظِيمُ ذُو الْجَلَالِ سُبْحَانَ اللهِ",
            textTurkish = "Ve hüve’l-aliyyü’l-azîmü zü’l-celâli sübhânallâh.",
            meaning = "O çok yücedir, çok büyüktür; celâl sahibi Allah her türlü noksanlıktan münezzehtir.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "6. Elhamdülillâh (33 Defa)",
            textArabic = "سُبْحَانَ الْبَاقِي دَائِمًا الْحَمْدُ للهِ",
            textTurkish = "Sübhâne’l-bâkî dâimenil-hamdülillâh.",
            meaning = "Varlığı ebedî olan Allah noksan sıfatlardan uzaktır; daima hamd Allah’a mahsustur.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "7. Allâhuekber (33 Defa)",
            textArabic = "رَبِّ الْعَالَمِينَ تَعَالَى شَانُهُ اَللهُ أَكْبَرُ",
            textTurkish = "Rabbi’l-âlemîne teâlâ şânühü Allâhü ekber.",
            meaning = "Âlemlerin Rabbi olan Allah’ın şânı ne yücedir; Allah en büyüktür.",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "8. Tahlil ve Dua",
            textArabic = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
            textTurkish = "Lâ ilâhe illallâhü vahdehû lâ şerîke leh, lehü’l-mülkü ve lehü’l-hamdü yuhyî ve yümît, ve hüve hayyün lâ yemût, biyedihi’l-hayru ve hüve alâ külli şey’in kadîr ve ileyhi’l-masîr.",
            meaning = "Allah’tan başka ilâh yoktur; O tektir, ortağı yoktur. Mülk O’nundur, hamd O’na mahsustur. Hayatı veren ve öldüren O’dur. O hiç ölmeyen Hayy’dır. Bütün hayır O’nun elindedir ve O her şeye hakkıyla kādirdir. Dönüş de O’nadır. (Dua vakti: Ellerinizi açarak dua ediniz).",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "9. Kelime-i Tevhid (33 Defa)",
            textArabic = "فَاعْلَمْ أَنَّهُ: لَا إِلٰهَ إِلَّا اللهُ",
            textTurkish = "Fa’lem ennehû: Lâ ilâhe illallâh. (33 defa tekrar edilir. Sonunda: Muhammedü’r-resûlüllâhi sallâllâhü teâlâ aleyhi ve sellem).",
            meaning = "Bil ki: Allah'tan başka ilâh yoktur! (33 defa zikredilir. Sonunda 'Muhammed Allah’ın elçisidir, Allah ona salât ve selâm eylesin' denir).",
            type = StepType.COUNTER,
            targetCount = 33
        ),
        NamazTesbihatiStep(
            title = "10. Salavat-ı Şerife",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\n\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا (٣ defa)\n\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ\n\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا رَسُولَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا حَب۪يبَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ\n\nاَللّٰهُمَّ صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِوَالِدَيْنَا وَ بِجَم۪يعِ الْمُؤْمِن۪ينَ وَ الْمُؤْمِنَاتِ يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nİnnallâhe ve melâiketehû yüsallûne ale’n-nebiy. Yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk.\n\nAllâhümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, biadedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. (3 defa)\n\nSalli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemîı’l-enbiyâi ve’l-mürselîn ve alâ âli küllin ve sahbi küllin ecmaîn. Âmin ve’l-hamdü lillâhi Rabbi’l-âlemîn.\n\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Resûlallâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ Habîballâh.\nElfü elfi salâtin ve elfü elfi selâmin aleyke yâ emîne vahyillâh.\n\nAllâhümme salli ve sellim ve bârik alâ seyyidinâ Muhammedin ve alâ âlihî ve ashâbihî biadedi evrâkı’l-eşcâr ve emvâci’l-bihâr ve katarâti’l-emtâr. Vagfir lenâ verhamnâ veltuf binâ ve bivâlideynâ ve bi-cemîı’l-mü’minîne ve’l-mü’minât yâ ilâhenâ bikülli salâtin minhâ. Eşhedü en lâ ilâhe illallâh ve eşhedü enne Muhammeder-resûlüllâh sallâllâhü teâlâ aleyhi ve sellem.",
            meaning = "Şüphesiz Allah ve melekleri Peygamber’e salât ederler. Ey iman edenler, siz de ona tam bir teslimiyetle salât ve selâm getirin! Allah’ım! Efendimiz Muhammed’e ve âline bütün dertler ve devalar adedince salât, bereket ve selâm eyle. Ağaçların yaprakları, denizlerin dalgaları ve yağmurun damlaları adedince salât ve selâm olsun. Milyonlarca salât ve selâm senin üzerine olsun ey Allah’ın Resulü!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "11. İsm-i A'zâm Duası",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nيَا جَم۪يلُ يَٓا اَللّٰهُ ٭ يَا قَر۪يبُ يَٓا اَللّٰهُ\nيَا مُج۪يبُ يَٓا اَللّٰهُ ٭ يَا حَب۪يبُ يَٓا اَللّٰهُ\nيَا رَؤُفُ يَٓا اَللّٰهُ ٭ يَا عَطُوفُ يَٓا اَللّٰهُ\nيَا مَعْرُوفُ يَٓا اَللّٰهُ ٭ يَا لَط۪يفُ يَٓا اَللّٰهُ\nيَا عَظ۪يمُ يَٓا اَللّٰهُ ٭ يَا حَنَّانُ يَٓا اَللّٰهُ\nيَا مَنَّانُ يَٓا اَللّٰهُ ٭ يَا دَيَّانُ يَٓا اَللّٰهُ\nيَا سُبْحَانُ يَٓا اَللّٰهُ ٭ يَٓا اَمَانُ يَٓا اَللّٰهُ\nيَا بُرْهَانُ يَٓا اَللّٰهُ ٭ يَا سُلْطَانُ يَٓا اَللّٰهُ\nيَا مُسْتَعَانُ يَٓا اَللّٰهُ ٭ يَا مُحْسِنُ يَٓا اَللّٰهُ\nيَا مُتَعَالُ يَٓا اَللّٰهُ ٭ يَا رَحْمٰنُ يَٓا اَللّٰهُ\nيَا رَح۪يمُ يَٓا اَللّٰهُ ٭ يَا كَر۪يمُ يَٓا اَللّٰهُ\nيَا مَج۪يدُ يَٓا اَللّٰهُ ٭ يَا فَرْدُ يَٓا اَللّٰهُ\nيَا وِتْرُ يَٓا اَللّٰهُ ٭ يَٓا اَحَدُ يَٓا اَللّٰهُ\nيَا صَمَدُ يَٓا اَللّٰهُ ٭ يَا مَحْمُودُ يَٓا اَللّٰهُ\nيَا صَادِقَ الْوَعْدِ يَٓا اَللّٰهُ ٭ يَا عَلِىُّ يَٓا اَللّٰهُ\nيَا غَنِىُّ يَٓا اَللّٰهُ ٭ يَا شَاف۪ى يَٓا اَللّٰهُ\nيَا كَاف۪ى يَٓا اَللّٰهُ ٭ يَا مُعَاف۪ى يَٓا اَللّٰهُ\nيَا بَاق۪ى يَٓا اَللّٰهُ ٭ يَا هَاد۪ى يَٓا اَللّٰهُ\nيَا قَادِرُ يَٓا اَللّٰهُ ٭ يَا سَاتِرُ يَٓا اَللّٰهُ\nيَا قَهَّارُ يَٓا اَللّٰهُ ٭ يَا جَبَّارُ يَٓا اَللّٰهُ\nيَا غَفَّارُ يَٓا اَللّٰهُ ٭ يَا فَتَّاحُ يَٓا اَللّٰهُ\n\nيَا رَبَّ السَّمٰوَاتِ وَ اْلاَرْضِ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ اَسْئَلُكَ بِحَقِّ هٰذِهِ اْلاَسْمَٓاءِ كُلِّهَٓا اَنْ تُصَلِّىَ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلٰٓى اٰلِ مُحَمَّدٍ وَ ارْحَمْ مُحَمَّدًا كَمَا صَلَّيْتَ وَ سَلَّمْتَ وَ بَارَكْتَ وَ رَحِمْتَ وَ تَرَحَّمْتَ عَلٰٓى اِبرَاه۪يمَ وَ عَلٰٓى اٰلِ اِبْرَاه۪يمَ فِى الْعَالَم۪ينَ رَبَّنَٓا اِنَّكَ حَم۪يدٌ مَج۪يدٌ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ وَ الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
            textTurkish = "Bismillâhirrahmânirrahîm.\nYâ Cemîlü yâ Allâh, yâ Karîbü yâ Allâh.\nYâ Mücîbü yâ Allâh, yâ Habîbü yâ Allâh.\nYâ Raûfü yâ Allâh, yâ Atûfü yâ Allâh.\nYâ Ma’rûfü yâ Allâh, yâ Latîfü yâ Allâh.\nYâ Azîmü yâ Allâh, yâ Hannânü yâ Allâh.\nYâ Mennânü yâ Allâh, yâ Deyyânü yâ Allâh.\nYâ Sübhânü yâ Allâh, yâ Emânü yâ Allâh.\nYâ Burhânü yâ Allâh, yâ Sultânü yâ Allâh.\nYâ Müsteânü yâ Allâh, yâ Muhsinü yâ Allâh.\nYâ Müteâlü yâ Allâh, yâ Rahmânü yâ Allâh.\nYâ Rahîmü yâ Allâh, yâ Kerîmü yâ Allâh.\nYâ Mecîdü yâ Allâh, yâ Ferdü yâ Allâh.\nYâ Vitrü yâ Allâh, yâ Ehadü yâ Allâh.\nYâ Samedü yâ Allâh, yâ Mahmûdü yâ Allâh.\nYâ Sâdıka’l-va’di yâ Allâh, yâ Aliyyü yâ Allâh.\nYâ Ganiyyü yâ Allâh, yâ Şâfî yâ Allâh.\nYâ Kâfî yâ Allâh, yâ Muâfî yâ Allâh.\nYâ Bâkî yâ Allâh, yâ Hâdî yâ Allâh.\nYâ Kâdirü yâ Allâh, yâ Sâtirü yâ Allâh.\nYâ Kahhârü yâ Allâh, yâ Cebbârü yâ Allâh.\nYâ Gaffârü yâ Allâh, yâ Fettâhu yâ Allâh.\n\nYâ Rabbe’s-semâvâti ve’l-ard! Yâ ze’l-celâli ve’l-ikrâm! Es’elüke bi-hakkı hâzihi’l-esmâi küllihâ en tusalliye alâ seyyidinâ Muhammedin ve alâ âli Muhammed, verham Muhammeden kemâ salleyte ve sellemte ve bârekte ve rahimte ve terahhamte alâ İbrâhîme ve alâ âli İbrâhîme fi’l-âlemîn. Rabbenâ inneke Hamîdün Mecîd. Bi-rahmetike yâ Erhame’r-râhimîn ve’l-hamdü lillâhi Rabbi’l-âlemîn.",
            meaning = "Ey Cemîl (güzellik sahibi) Allah! Ey Karîb (kullarına çok yakın) Allah! Ey Mücîb (dualara icabet eden) Allah! Ey Habîb (sevgili) Allah! ... Ey göklerin ve yerin Rabbi, ey celâl ve ikram sahibi! Bütün bu yüce isimlerinin hakkı için Efendimiz Muhammed’e ve onun âline salât ve rahmet eylemeni diliyoruz. Âlemlerin Rabbi Allah’a hamd olsun.",
            type = StepType.PRAYER,
            handGesture = "Eller yukarı açık şekilde hürmetle niyaz edilir."
        ),
        NamazTesbihatiStep(
            title = "12. Âmene'r-Rasûlü (Bakara 285-286)",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاٰمَنَ الرَّسُولُ بِمَٓا اُنْزِلَ اِلَيْهِ مِنْ رَبِّه۪ وَالْمُؤْمِنُونَۜ كُلٌّ اٰمَنَ بِاللّٰهِ وَمَلٰٓئِكَتِه۪ وَكُتُبِه۪ وَرُسُلِه۪ۜ لَا نُفَرِّقُ بَيْنَ اَحَدٍ مِنْ رُسُلِه۪۠ وَقَالُوا سَمِعْنَا وَاَطَعْنَا غُفْرَانَكَ رَبَّنَا وَاِلَيْكَ الْمَص۪يرُ ﴿٢٨٥﴾\nلَا يُكَلِّفُ اللّٰهُ نَفْسًا اِلَّا وُسْعَهَاۜ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْۜ رَبَّنَا لَا تُؤَاخِذْنَٓا اِنْ نَس۪ينَٓا اَوْ اَخْطَأْنَاۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَٓا اِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذ۪ينَ مِنْ قَبْلِنَاۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِه۪ۚ وَاعْفُ عَنَّا۠ وَاغْفِرْ لَنَا۠ وَارْحَمْنَا۠ اَنْتَ مَوْلٰينَا فَانْصُرْنَا عَلَى الْقَوْمِ الْكَافِر۪ينَ ﴿٢٨٦﴾",
            textTurkish = "Bismillâhirrahmânirrahîm.\nÂmene’r-rasûlü bimâ ünzile ileyhi min rabbihî ve’l-mü’minûn. Küllün âmene billâhi ve melâiketihî ve kütübihî ve rusülih. Lâ nüferriku beyne ehadin min rusülih. Ve kâlû semi’nâ ve eta’nâ gufrâneke rabbenâ ve ileyke’l-masîr.\nLâ yükellifullâhü nefsen illâ vüs’ahâ, lehâ mâ kesebet ve aleyhâ mektesebet. Rabbenâ lâ tüâhiznâ in nesînâ ev ahta’nâ. Rabbenâ ve lâ tahmil aleynâ ısran kemâ hameltehû alellezîne min kablinâ. Rabbenâ ve lâ tühammilnâ mâ lâ tâkate lenâ bih. Va’fü annâ vağfir lenâ verhamnâ, ente mevlânâ fansurnâ ale’l-kavmi’l-kâfirîn.",
            meaning = "Peygamber, Rabbinden kendisine indirilene iman etti, müminler de. Hepsi Allah’a, meleklerine, kitaplarına ve peygamberlerine inandı... Allah hiçbir kimseyi gücünün yetmediği bir şeyle yükümlü tutmaz... Rabbimiz, unutur veya hata edersek bizi sorumlu tutma! Bizi affet, bizi bağışla, bize acı! Sen bizim Mevlâmızsın; kâfirler topluluğuna karşı bize yardım et!",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "13. Fâtiha Sûresi",
            textArabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ ﴿١﴾ اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ ﴿٢﴾ اَلرَّحْمٰنِ الرَّحِيمِ ﴿٣﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٤﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٥﴾ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٦﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّآلِّينَ ﴿٧﴾",
            textTurkish = "Bismillâhirrahmânirrahîm. Elhamdü lillâhi rabbi’l-âlemîn. Er-rahmâni’r-rahîm. Mâliki yevmi’d-dîn. İyyâke na’büdü ve iyyâke neste’în. İhdina’s-sırâta’l-müstakîm. Sırâtallezîne en’amte aleyhim gayri’l-magdûbi aleyhim veleddâllîn. Âmin.",
            meaning = "Rahman ve Rahim olan Allah'ın adıyla. Hamd, Âlemlerin Rabbi Allah'a mahsustur. O, Rahman'dır, Rahim'dir. Hesap ve ceza gününün mâlikidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet; kendilerine nimet verdiklerinin yoluna, gazaba uğrayanların ve sapanlarınkine değil.",
            type = StepType.PRAYER
        ),
        NamazTesbihatiStep(
            title = "14. Tamamlandı",
            textArabic = "",
            textTurkish = "Kabûl Olsun",
            meaning = "Namaz tesbihatınız tamamlanmıştır. Allah dualarınızı, zikirlerinizi ve tesbihatınızı kabul eylesin!",
            type = StepType.COMPLETED
        )
    )

    fun getSteps(prayerTime: TesbihatPrayerTime): List<NamazTesbihatiStep> {
        return when (prayerTime) {
            TesbihatPrayerTime.SABAH -> sabahSteps
            TesbihatPrayerTime.OGLE -> ogleSteps
            TesbihatPrayerTime.IKINDI -> ikindiSteps
            TesbihatPrayerTime.AKSAM -> aksamSteps
            TesbihatPrayerTime.YATSI -> yatsiSteps
        }
    }
}
