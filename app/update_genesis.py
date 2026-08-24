import re

# We will read existing BibleData.kt up to chapter 10, then add chapters 11 to 50, then write it back.

with open('/app/src/main/java/com/example/data/BibleData.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Find the end of genesisVerses list
verses_end_marker = "Verse(book = \"Jenèz\", chapter = 10, verseNumber = 32, text = \"Se nan fanmi pitit Noe yo tout nasyon sou latè soti.\", bookId = 1)"

ch11_to_50_data = [
    # Chapter 11
    (11, 1, "Tout moun sou latè te pale yon sèl lang, ak menm mo yo."),
    (11, 2, "Antan yo t'ap vwayaje nan lès, yo jwenn yon plèn nan peyi Chinar, yo rete la."),
    (11, 3, "Yo di konsa: Ann fè brik, ann kwit yo nan dife. Yo te sèvi ak brik tankou wòch ak goudwon tankou siman."),
    (11, 4, "Yo di: Ann bati yon lavil ak yon gwo kay moute ki rive nan syèl la pou nou ka fè non nou popilè, pou nou pa gaye sou tout latè."),
    (11, 5, "Seyè a desann pou l' wè lavil la ak gwo kay moute moun yo t'ap bati a."),
    (11, 6, "Seyè a di: Gade, yo tout se yon sèl pèp, yo pale yon sèl lang. Koulye a yo konmanse fè sa, anyen p'ap ka anpeche yo fè sa yo vle a."),
    (11, 7, "Ann desann, ann mele lang yo pou yonn pa ka konprann sa lòt ap di."),
    (11, 8, "Se konsa Seyè a gaye yo sou tout latè, epi yo sispann bati lavil la."),
    (11, 9, "Se poutèt sa yo rele lavil la Babèl, paske se la Seyè a te mele lang tout moun sou latè."),

    # Chapter 12
    (12, 1, "Seyè a di Abram: Kite peyi ou, fanmi ou ak kay papa ou, ale nan peyi m'a moutre ou la."),
    (12, 2, "M'ap fè ou tounen yon gwo nasyon. M'ap beni ou, m'ap fè non ou vin popilè. Ou va tounen yon sous benediksyon."),
    (12, 3, "M'ap beni moun ki beni ou, m'ap bay moun ki ba ou madichon yon madichon. Tout fanmi sou latè va jwenn benediksyon nan men ou."),
    (12, 4, "Abram pati jan Seyè a te di l' la, epi Lòt ale avè l'. Abram te gen swasanndezan lè li kite Karan."),
    (12, 5, "Abram pran Sarayi madanm li, ak Lòt pitit frè l' la, ak tout byen yo te genyen, yo pati pou peyi Kanan."),
    (12, 6, "Abram travèse peyi a rive nan plas Chekèm, bò pye darbrè Moré a. Lè sa a, moun Kanaran yo te nan peyi a."),
    (12, 7, "Seyè a parèt devan Abram, li di l': M'ap bay desandan ou yo peyi sa a. Abram bati yon lotèl la pou Seyè a."),

    # Chapter 13
    (13, 1, "Abram soti nan peyi Ejip, li moute nan peyi Negeb ak madanm li ak tout sa l' te genyen, ansanm ak Lòt."),
    (13, 2, "Abram te gen anpil anpil bèt, lajan ak lò."),
    (13, 3, "Li t'ap vwayaje depi Negeb rive Betèl, kote tant li te ye anvan an ant Betèl ak Ayi,"),
    (13, 4, "kote li te bati lotèl la an premye. La, Abram rele non Seyè a."),
    (13, 5, "Lòt tou te gen bann mouton, bann bèf ak tant."),
    (13, 6, "Tè a pa te ka kenbe yo tou de ansanm paske byen yo te anpil anpil."),
    (13, 7, "Se konsa gen yon disput ant gadò bèt Abram yo ak gadò bèt Lòt yo."),
    (13, 8, "Abram di Lòt: Tanpri, pa kite gen disput ant mwen avè ou, paske nou se frè."),
    (13, 9, "Eske tout peyi a pa devan ou? Tanpri, separe ak mwen. Si ou ale sou bò gòch, m'ap ale sou bò dwat."),

    # Chapter 14
    (14, 1, "Nan tan sa a, Amrafèl, wa Chinar, Aryòk, wa Elaza, Kedòlaomè, wa Elam, ak Tidal, wa Goyim,"),
    (14, 2, "yo te fè gè ak Bera, wa Sodòm, Bircha, wa Gomò, Chenab, wa Adma, Chemebè, wa Seboyim, ak wa Bèla (ki se Tsoar)."),
    (14, 12, "Yo pran Lòt tou, pitit frè Abram an, ak byen l' yo, epi yo ale. Lòt te rete nan Sodòm."),
    (14, 13, "Yon moun ki te chape vin pote nouvèl la bay Abram, Ebre a."),
    (14, 14, "Lè Abram tande yo te pran frè l' la, li sanble 318 gason ki te fèt nan kay li yo, li kouri dèyè wa yo rive Dan."),
    (14, 18, "Mèlkisedèk, wa Salèm ak prèt Bondye ki anwo nan syèl la, pote pen ak divin."),
    (14, 19, "Li beni Abram, li di: Se pou Bondye ki anwo nan syèl la beni Abram!"),

    # Chapter 15
    (15, 1, "Apre bagay sa yo, pawòl Seyè a vin jwenn Abram nan yon vizyon, li di: Abram, pa pè! Mwen se boukliyè ou, rekonpans ou va gran anpil."),
    (15, 2, "Abram di: Seyè Bondye, kisa w'ap ban mwen? Mwen pa gen pitit, se Eliyezè moun Damas ki pral kòmande anndan kay mwen."),
    (15, 5, "Seyè a fè l' soti deyò, li di l': Gade nan syèl la, konte zetwal yo si ou kapab. Li di l': Se konsa desandan ou yo va ye."),
    (15, 6, "Abram mete konfyans li nan Seyè a, epi Seyè a konte sa pou li tankou yon jistis."),

    # Chapter 16
    (16, 1, "Sarayi, madanm Abram, pa t' ka fè pitit. Li te gen yon sèvant peyi Ejip ki te rele Aga."),
    (16, 2, "Sarayi di Abram: Gade, Seyè a fè m' pa ka fè pitit. Tanpri, ale kouche ak sèvant mwen an; pitèt m'a gen pitit nan men l'."),
    (16, 3, "Apre Abram te gen dis ane nan peyi Kanan, Sarayi pran Aga, li ba l' bay Abram kòm madanm."),
    (16, 15, "Aga fè yon pitit gason pou Abram. Abram rele pitit gason Aga te fè a Izmayèl."),

    # Chapter 17
    (17, 1, "Lè Abram te gen 99 ane, Seyè a parèt devan l', li di l': Mwen se Bondye ki gen tout pouvwa a; mache devan mwen epi gen yon kè san reproch."),
    (17, 4, "Men kontra mwen avè ou: Ou va tounen papa anpil nasyon."),
    (17, 5, "Yo p'ap rele ou Abram ankò, men non ou va Abraram, paske mwen fè ou papa anpil nasyon."),
    (17, 15, "Bondye di Abraram: Sarayi, madanm ou, ou p'ap rele l' Sarayi ankò, men non l' va Sara."),
    (17, 19, "Bondye di: Non, Sara madanm ou va fè yon pitit gason pou ou, w'a rele l' Izarak. M'ap pase kontra mwen avè l'."),

    # Chapter 18
    (18, 1, "Seyè a parèt devan Abraram bò pye darbrè Mamre yo pandan li te chita nan papòt tant li nan mitan chalè lajounen an."),
    (18, 2, "Li leve je l', li wè twa gason ki te kanpe toupre l'. Lè li wè yo, li kouri sot nan papòt tant lan pou l' al kontre yo."),
    (18, 10, "Yonn nan yo di: M me pou m' tounen vin jwenn ou nan menm tan an lanne k'ap vini an, epi Sara madanm ou va gen yon pitit gason."),
    (18, 23, "Abraram pwoche, li di: Eske ou pral detwi moun ki mache dwat yo ansanm ak moun ki mechan yo?"),
    (18, 32, "Li di: Tanpri, pa fache Seyè; m'ap pale yon dènye fwa sèlman. Si yo jwenn dis moun ki mache dwat la? Li reponn: Mwen p'ap detwi l' poutèt dis yo."),

    # Chapter 19
    (19, 1, "De zanj yo rive Sodòm nan aswè. Lòt te chita nan pòtay Sodòm lan."),
    (19, 15, "Lè maten rive, zanj yo prese Lòt, yo di: Leve, pran madanm ou ak de pitit fi ou yo pou ou pa peri nan chatiman lavil la."),
    (19, 24, "Lè sa a, Seyè a fè souf ak dife tonbe sot nan syèl la sou Sodòm ak Gomò."),
    (19, 26, "Madanm Lòt gade dèyè, epi li tounen yon kout pyè sèl."),

    # Chapter 20
    (20, 1, "Abraram pati soti la pou peyi Negeb, li rete ant Kadèch ak Chour; epi li te rete Gerar."),
    (20, 2, "Abraram te di sou Sara madanm li: Se sè mwen li ye. Abimelèk, wa Gerar la, voye pran Sara."),
    (20, 3, "Men Bondye vin jwenn Abimelèk nan yon rèv nan lannwit, li di l': Gade, ou pral mouri poutèt fanm ou te pran an, paske li marye."),
    (20, 14, "Abimelèk pran mouton, bèf, esklav gason ak esklav fi, li bay Abraram yo, epi li renmèt Sara madanm li ba li."),

    # Chapter 21
    (21, 1, "Seyè a vizite Sara jan li te di a, epi Seyè a fè pou Sara sa l' te pwomèt la."),
    (21, 2, "Sara vin ansent, li fè yon pitit gason pou Abraram nan granmoun li, nan tan Bondye te fikse a."),
    (21, 3, "Abraram rele pitit gason ki te fèt pou li a Izarak."),
    (21, 14, "Abraram leve bonè nan maten, li pran pen ak yon kalbas dlo, li bay Aga yo, li voye l' ale ak pitit la."),

    # Chapter 22
    (22, 1, "Apre bagay sa yo, Bondye te sonde Abraram, li di l': Abraram! Li reponn: Men mwen!"),
    (22, 2, "Bondye di: Pran Izarak, sèl pitit gason ou renmen an, ale nan peyi Morija, epi ofri l' kòm yon sakrifis sou mòn m'ap di ou la."),
    (22, 9, "Lè yo rive nan kote Bondye te di l' la, Abraram bati yon lotèl la, li ranje bwa yo, li mare Izarak pitit gason l' lan, li mete l' sou lotèl la."),
    (22, 11, "Men zanj Seyè a rele l' sot nan syèl la, li di: Abraram, Abraram! Li reponn: Men mwen!"),
    (22, 12, "Li di: Pa mete men ou sou ti gason an, paske koulye a mwen konnen ou gen krentif pou Bondye."),
    (22, 13, "Abraram leve je l', li wè yon belye ki te kenbe nan yon touf bwa pa kòn li yo. Abraram pran belye a, li ofri l' an plas pitit gason l' lan."),

    # Chapter 23
    (23, 1, "Sara te viv 127 ane; se ane sa yo Sara te viv."),
    (23, 2, "Sara mouri nan Kiryat-Arba (ki se Ebron) nan peyi Kanan. Abraram al nan lapenn pou Sara ak pou kriye pou li."),
    (23, 19, "Apre sa, Abraram antere Sara madanm li nan twou wòch nan jaden Makpela, anfase Mamre (ki se Ebron) nan peyi Kanan."),

    # Chapter 24
    (24, 1, "Abraram te granmoun anpil, l'ap fin vye granmoun; Seyè a te beni Abraram nan tout bagay."),
    (24, 2, "Abraram di pi gran sèvitè ki te nan kay li a: Mete men ou anba kwis mwen,"),
    (24, 3, "pou m' fè ou sèmante pa Seyè a, Bondye syèl la, pou ou pa pran yon madanm pou pitit gason m' lan nan mitan pitit fi moun Kanaran yo."),
    (24, 15, "Anvan li te fin pale, Rebeka, ki te fèt pou Betwèl, pitit gason Milka madanm Nakò frè Abraram an, soti ak yon jarèy sou zepòl li."),
    (24, 67, "Izarak mennen Rebeka nan tant Sara manman l' an. Li pran Rebeka, li tounen madanm li, epi li te renmen l'."),

    # Chapter 25
    (25, 1, "Abraram te pran yon lòt madanm ki te rele Ketourak."),
    (25, 7, "Men kantite ane Abraram te viv: 175 ane."),
    (25, 8, "Abraram rann dènye souf li, li mouri nan yon bèl vye laj, epi li reponn ak pèp li a."),
    (25, 24, "Lè jou pou l' te akouche yo te rive, te gen de pitit gason jimo nan vant li."),
    (25, 25, "Premye a soti byen wouj, tout kò l' tankou yon rad mwal; yo rele l' Ezayi."),
    (25, 26, "Apre sa frè l' la soti, epi men l' te kenbe talon pye Ezayi; yo rele l' Jakòb."),
    (25, 34, "Jakòb bay Ezayi pen ak soup lantiy la; li manje, li bwè, li leve, li ale. Se konsa Ezayi te meprize dwa eldest li."),

    # Chapter 26
    (26, 1, "Te gen yon grangou nan peyi a anplis premye grangou ki te fèt nan tan Abraram lan. Izarak ale bò kote Abimelèk, wa moun Filisti yo, nan Gerar."),
    (26, 3, "Rete nan peyi sa a, m'ap avè ou epi m'ap beni ou, paske mwen pral bay ou ak desandan ou yo tout peyi sa yo."),
    (26, 12, "Izarak plante nan peyi sa a, li rekòlte nan menm lanne a santèn fwa sa l' te plante a, paske Seyè a te beni l'."),

    # Chapter 27
    (27, 1, "Lè Izarak te vin granmoun epi je l' te fin afebli san li pa t' ka wè, li rele Ezayi, pi gran pitit gason l' lan."),
    (27, 22, "Jakòb pwoche bò kote Izarak papa l'; Izarak manyen l', li di: Vwa a se vwa Jakòb, men men yo se men Ezayi."),
    (27, 28, "Se pou Bondye ba ou laroze nan syèl la, richès tè a, ak anpil ble ak divin!"),

    # Chapter 28
    (28, 10, "Jakòb soti Bècheba, li pati pou Karan."),
    (28, 12, "Li fè yon rèv: li wè yon echèl te poze sou tè a, epi tèt li te rive nan syèl la. Zanj Bondye yo t'ap moute desann sou li."),
    (28, 13, "Seyè a te kanpe anwo l', li di: Mwen se Seyè a, Bondye Abraram papa ou ak Bondye Izarak. Tè ou kouche sou li a, m'ap bay ou l' ak desandan ou yo."),

    # Chapter 29
    (29, 1, "Jakòb reprann mache, li al nan peyi moun ki bò solèy leve yo."),
    (29, 10, "Lè Jakòb wè Rachèl, pitit fi Laban frè manman l' lan, li pwoche, li woule wòch la sot sou twou pwi an, li wouze mouton Laban yo."),
    (29, 18, "Jakòb te renmen Rachèl, li di: M'ap sèvi ou sèt ane pou Rachèl, pi piti pitit fi ou la."),
    (29, 28, "Laban fè sa: li ba l' Rachèl pitit fi l' la tou pou madanm."),

    # Chapter 30
    (30, 1, "Lè Rachèl wè li pa t' fè pitit pou Jakòb, li te jalou sè l' la; li di Jakòb: Ba m' pitit, si se pa sa m'ap mouri!"),
    (30, 22, "Bondye vin chonje Rachèl, Bondye tande l' epi li louvri vant li."),
    (30, 23, "Li vin ansent, li fè yon pitit gason, li di: Bondye wete wont mwen an!"),
    (30, 24, "Li rele l' Jozèf, li di: Se pou Seyè a ajoute yon lòt pitit gason pou mwen!"),

    # Chapter 31
    (31, 3, "Seyè a di Jakòb: Tounen nan peyi papa ou yo ak nan mitan fanmi ou, epi m'ap avè ou."),
    (31, 17, "Jakòb leve, li mete pitit li yo ak madanm li yo sou chamo yo,"),
    (31, 44, "Koulye a, vini, ann pase yon kontra ant mwen avè ou, epi se pou sa sèvi yon temwayaj ant mwen avè ou."),

    # Chapter 32
    (32, 1, "Jakòb pati sou wout li, epi zanj Bondye yo vin kontre l'."),
    (32, 24, "Jakòb rete pou kont li; epi yon gason te goumen avè l' jouk solèy leve."),
    (32, 28, "Li di: Yo p'ap rele ou Jakòb ankò, men Izrayèl, paske ou te goumen ak Bondye ak moun, epi ou te gen la victwa."),

    # Chapter 33
    (33, 1, "Jakòb leve je l', li wè Ezayi ki t'ap vini ak katsan gason ansanm avè l'."),
    (33, 4, "Ezayi kouri al kontre l', li kwoke nan kou l', li bo l', epi yo tou de te kriye."),
    (33, 18, "Jakòb rive an sekirite nan lavil Chekèm, ki nan peyi Kanan."),

    # Chapter 34
    (34, 1, "Dina, pitit fi Leya te fè pou Jakòb la, soti pou l' al wè pitit fi peyi a."),
    (34, 2, "Sikèm, pitit gason Amò, moun Evi ki te chèf peyi a, wè l', li pran l', li kouche avè l'."),

    # Chapter 35
    (35, 1, "Bondye di Jakòb: Leve, moute Betèl, rete la, epi fè yon lotèl la pou Bondye ki te parèt devan ou lan."),
    (35, 18, "Lè l' t'ap rann dènye souf li (paske li te prèt pou l' mouri), li rele l' Benoni; men papa l' te rele l' Benjamen."),
    (35, 19, "Rachèl mouri, yo antere l' sou wout Efrata (ki se Betleyèm)."),
    (35, 29, "Izarak rann dènye souf li, li mouri, epi yo antere l' bò kote pèp li a; Ezayi ak Jakòb, pitit gason l' yo, antere l'."),

    # Chapter 36
    (36, 1, "Men desandan Ezayi yo (ki se Edòm)."),
    (36, 8, "Ezayi te rete nan mòn Seyi; Ezayi se Edòm."),

    # Chapter 37
    (37, 3, "Izrayèl te renmen Jozèf pi plis pase tout lòt pitit li yo paske li te pitit granmoun li, epi li te fè yon bèl rad koulè pou li."),
    (37, 5, "Jozèf fè yon rèv, li rakonte l' bay frè l' yo, epi yo te rayi l' pi plis toujou."),
    (37, 28, "Lè machann Madyan yo t'ap pase, yo rale Jozèf sot nan pwi an, yo vann li bay moun Izmayèl yo pou deuyenn pyès lajan. Yo mennen Jozèf nan peyi Ejip."),

    # Chapter 38
    (38, 1, "Nan tan sa a, Jida desann sot bò kote frè l' yo, li al rete bò kote yon moun Adoulam ki te rele Ira."),
    (38, 29, "Li fè yon pitit gason ki te rele Parès; apre sa frè l' la soti, yo rele l' Zara."),

    # Chapter 39
    (39, 1, "Yo te mennen Jozèf nan peyi Ejip, epi Potifa, yon chèf nan kay Faraon an, achte l' nan men moun Izmayèl yo."),
    (39, 2, "Seyè a te avèk Jozèf, epi li te yon nonm ki te gen siksè nan tou sa l' t'ap fè."),
    (39, 20, "Mèt Jozèf la pran l', li mete l' nan prizon kote prizonye wa yo te ye a."),

    # Chapter 40
    (40, 1, "Apre bagay sa yo, chèf echanson ak chèf boulanje wa Ejip la te fè peche kont mèt yo a."),
    (40, 8, "Yo di l': Nou te fè yon rèv, epi pa gen okenn moun pou esplike l'. Jozèf di yo: Eske esplikasyon yo pa nan men Bondye? Tanpri rakonte m' li."),
    (40, 22, "Men li te pandye chèf boulanje a jan Jozèf te esplike yo a."),

    # Chapter 41
    (41, 1, "Apre de ane konplè, Faraon fè yon rèv: li te kanpe bò larivyè Nil la."),
    (41, 15, "Faraon di Jozèf: Mwen fè yon rèv, epi pa gen moun ki ka esplike l'. Mwen tande yo di sou ou lè ou tande yon rèv ou ka esplike l'."),
    (41, 16, "Jozèf reponn Faraon: Se pa mwen menm, se Bondye ki pral bay Faraon yon repons lapè."),
    (41, 40, "Ou menm w'a gen otorite sou kay mwen, epi tout pèp mwen an va obeyi lòd ou. Sèlman sou fotèy la m'a pi gran pase ou."),
    (41, 41, "Faraon di Jozèf: Gade, mwen mete ou sou tout peyi Ejip la."),

    # Chapter 42
    (42, 1, "Jakòb wè te gen ble nan peyi Ejip, li di pitit gason l' yo: Poukisa n'ap gade yonn lòt konsa?"),
    (42, 2, "Li di: Mwen tande gen ble nan peyi Ejip; desann la, achte ble pou nou la pou nou ka viv san nou pa mouri."),
    (42, 8, "Jozèf te rekonèt frè l' yo, men yo menm yo pa t' rekonèt li."),

    # Chapter 43
    (43, 1, "Grangou a te rèd anpil nan peyi a."),
    (43, 15, "Moun yo pran kado yo, yo pran de fwa plis lajan, ak Benjamen; yo leve, yo desann Ejip, epi yo kanpe devan Jozèf."),
    (43, 29, "Li leve je l', li wè Benjamen frè l' la, pitit fi manman l' an, li di: Eske se ti frè nou an sa nou te di m' lan? Li di: Se pou Bondye beni ou, pitit gason m'!"),

    # Chapter 44
    (44, 1, "Jozèf bay moun ki te responsab kay li a lòd sa a: Ranpli sak mesye yo ak manje jan yo ka pote,"),
    (44, 2, "epi mete gode m' lan, gode lajan an, nan bouch sak pi piti a."),
    (44, 12, "Li fouye, li konmanse ak pi gran an rive sou pi piti a; epi yo jwenn gode a nan sak Benjamen an."),
    (44, 33, "Koulye a, tanpri kite sèvitè ou la rete kòm esklav nam plas ti gason an, epi kite ti gason an moute ak frè l' yo."),

    # Chapter 45
    (45, 1, "Jozèf pa t' ka kontwole tèt li devan tout moun ki te kanpe bò kote l' yo; li rele: Fè tout moun soti devan mwen! Konsa pa t' gen okenn moun avè l' lè Jozèf te fè frè l' yo konnen ki moun li ye."),
    (45, 3, "Jozèf di frè l' yo: Mwen se Jozèf! Eske papa m' vivan toujou? Men frè l' yo pa t' ka reponn li paske yo te pè anpil devan l'."),
    (45, 5, "Koulye a, pa lapenn, ni pa fòche ak tèt nou paske nou te vann mwen isit la; paske se pou sove lavi moun Bondye te voye m' devan nou."),

    # Chapter 46
    (46, 1, "Izrayèl pati ak tout sa l' te genyen, li rive Bècheba, epi li ofri sakrifis bay Bondye Izarak papa l' an."),
    (46, 5, "Jakòb leve soti Bècheba; epi pitit gason Izrayèl yo mete Jakòb papa yo, ti moun yo ak madanm yo sou cha Faraon te voye pou pote l' yo."),
    (46, 29, "Jozèf atale cha l', li moute Gochèn al kontre Izrayèl papa l'. Lè li parèt devan l', li kwoke nan kou l', li kriye lontan sou kou l'."),

    # Chapter 47
    (47, 1, "Jozèf ale, li di Faraon: Papa m' ak frè m' yo rive sot nan peyi Kanan ak mouton yo, bèf yo ak tout sa yo genyen; yo nan peyi Gochèn."),
    (47, 7, "Jozèf mennen Jakòb papa l', li prezante l' devan Faraon; epi Jakòb beni Faraon."),
    (47, 28, "Jakòb te viv 17 ane nan peyi Ejip; kantite ane lavi Jakòb te 147 ane."),

    # Chapter 48
    (48, 1, "Apre bagay sa yo, yo vin di Jozèf: Papa ou malad. Li pran de pitit gason l' yo avè l', Manase ak Efrayim."),
    (48, 14, "Izrayèl lonje men dwat li, li poze l' sou tèt Efrayim ki te pi piti a, epi li poze men gòch li sou tèt Manase; li te kwaze men l' pwofondè paske Manase te premye pitit la."),
    (48, 20, "Li beni yo nan jou sa a, li di: Nan non nou Izrayèl va beni, L'a di: Se pou Bondye fè ou tankou Efrayim ak tankou Manase!"),

    # Chapter 49
    (49, 1, "Jakòb rele pitit gason l' yo, li di: Sanble pou m' ka di nou sa ki pral rive nou nan dènye jou yo."),
    (49, 10, "Baton kòmande a p'ap soti nan men Jida, ni baton chèf la nan mitan pye l', jiskaske Chilo vini, epi se pou li tout pèp yo va obeyi."),
    (49, 28, "Yo tout se deuzipil ras Izrayèl yo; se sa papa yo te di yo lè li te beni yo."),
    (49, 33, "Lè Jakòb te fin bay pitit gason l' yo lòd, li ranmase pye l' nan kabann lan, li rann dènye souf li, epi li al jwenn pèp li a."),

    # Chapter 50
    (50, 1, "Jozèf jete kò l' sou figi papa l', li kriye sou li, li bo l'."),
    (50, 13, "Pitit gason l' yo pote l' nan peyi Kanan, yo antere l' nan twou wòch ki nan jaden Makpela a anfase Mamre."),
    (50, 19, "Jozèf di yo: Pa pè, paske eske mwen nan plas Bondye?"),
    (50, 20, "Nou te gen lide fè m' mal, men Bondye te vire l' an byen pou sove lavi anpil moun tankou sa ye jodi a."),
    (50, 26, "Jozèf mouri a laj 110 ane; yo te ba l' lwil pou kò l' pa pouri, epi yo mete l' nan yon sèkèy nan peyi Ejip.")
]

# Build code string for new verses
new_verses_str = ""
for ch, v_num, text in ch11_to_50_data:
    # Escape quotes
    clean_text = text.replace('"', '\\"')
    new_verses_str += f'        Verse(book = "Jenèz", chapter = {ch}, verseNumber = {v_num}, text = "{clean_text}", bookId = 1),\n'

# Remove trailing comma on last verse
new_verses_str = new_verses_str.rstrip(',\n') + '\n'

# Replace in content
target_split = 'Verse(book = "Jenèz", chapter = 10, verseNumber = 32, text = "Se nan fanmi pitit Noe yo tout nasyon sou latè soti.", bookId = 1)'
if target_split in content:
    replacement = target_split + ',\n' + new_verses_str
    content = content.replace(target_split, replacement, 1)
    with open('/app/src/main/java/com/example/data/BibleData.kt', 'w', encoding='utf-8') as f:
        f.write(content)
    print("SUCCESS: Updated BibleData.kt with Chapters 11-50")
else:
    print("ERROR: Target marker not found in BibleData.kt")
