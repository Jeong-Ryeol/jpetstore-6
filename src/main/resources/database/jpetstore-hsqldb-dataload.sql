--
--    Copyright 2010-2025 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       https://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

INSERT INTO sequence VALUES('ordernum', 1000);

INSERT INTO signon VALUES('j2ee','j2ee');
INSERT INTO signon VALUES('ACID','ACID');

INSERT INTO account VALUES('j2ee','yourname@yourdomain.com','ABC', 'XYX', 'OK', '901 San Antonio Road', 'MS UCUP02-206', 'Palo Alto', 'CA', '94303', 'USA',  '555-555-5555', 30, 'Engineer', 'MORE_THAN_6', 'APARTMENT', 'BETWEEN_100K_300K', false);
INSERT INTO account VALUES('ACID','acid@yourdomain.com','ABC', 'XYX', 'OK', '901 San Antonio Road', 'MS UCUP02-206', 'Palo Alto', 'CA', '94303', 'USA',  '555-555-5555', 25, 'Designer', 'TWO_TO_SIX', 'STUDIO', 'UNDER_100K', false);

INSERT INTO profile VALUES('j2ee','english','DOGS',1,1);
INSERT INTO profile VALUES('ACID','english','CATS',1,1);

INSERT INTO bannerdata VALUES ('FISH','<image src="../images/banner_fish.gif">');
INSERT INTO bannerdata VALUES ('CATS','<image src="../images/banner_cats.gif">');
INSERT INTO bannerdata VALUES ('DOGS','<image src="../images/banner_dogs.gif">');
INSERT INTO bannerdata VALUES ('REPTILES','<image src="../images/banner_reptiles.gif">');
INSERT INTO bannerdata VALUES ('BIRDS','<image src="../images/banner_birds.gif">');

INSERT INTO category VALUES ('FISH','Fish','<image src="../images/fish_icon.gif"><font size="5" color="blue"> Fish</font>');
INSERT INTO category VALUES ('DOGS','Dogs','<image src="../images/dogs_icon.gif"><font size="5" color="blue"> Dogs</font>');
INSERT INTO category VALUES ('REPTILES','Reptiles','<image src="../images/reptiles_icon.gif"><font size="5" color="blue"> Reptiles</font>');
INSERT INTO category VALUES ('CATS','Cats','<image src="../images/cats_icon.gif"><font size="5" color="blue"> Cats</font>');
INSERT INTO category VALUES ('BIRDS','Birds','<image src="../images/birds_icon.gif"><font size="5" color="blue"> Birds</font>');

INSERT INTO product VALUES ('FI-SW-01','FISH','Angelfish','<image src="../images/fish1.gif">Salt Water fish from Australia');
INSERT INTO product VALUES ('FI-SW-02','FISH','Tiger Shark','<image src="../images/fish4.gif">Salt Water fish from Australia');
INSERT INTO product VALUES ('FI-FW-01','FISH', 'Koi','<image src="../images/fish3.gif">Fresh Water fish from Japan');
INSERT INTO product VALUES ('FI-FW-02','FISH', 'Goldfish','<image src="../images/fish2.gif">Fresh Water fish from China');
INSERT INTO product VALUES ('K9-BD-01','DOGS','Bulldog','<image src="../images/dog2.gif">Friendly dog from England');
INSERT INTO product VALUES ('K9-PO-02','DOGS','Poodle','<image src="../images/dog6.gif">Cute dog from France');
INSERT INTO product VALUES ('K9-DL-01','DOGS', 'Dalmation','<image src="../images/dog5.gif">Great dog for a Fire Station');
INSERT INTO product VALUES ('K9-RT-01','DOGS', 'Golden Retriever','<image src="../images/dog1.gif">Great family dog');
INSERT INTO product VALUES ('K9-RT-02','DOGS', 'Labrador Retriever','<image src="../images/dog5.gif">Great hunting dog');
INSERT INTO product VALUES ('K9-CW-01','DOGS', 'Chihuahua','<image src="../images/dog4.gif">Great companion dog');
INSERT INTO product VALUES ('RP-SN-01','REPTILES','Rattlesnake','<image src="../images/snake1.gif">Doubles as a watch dog');
INSERT INTO product VALUES ('RP-LI-02','REPTILES','Iguana','<image src="../images/lizard1.gif">Friendly green friend');
INSERT INTO product VALUES ('FL-DSH-01','CATS','Manx','<image src="../images/cat2.gif">Great for reducing mouse populations');
INSERT INTO product VALUES ('FL-DLH-02','CATS','Persian','<image src="../images/cat1.gif">Friendly house cat, doubles as a princess');
INSERT INTO product VALUES ('AV-CB-01','BIRDS','Amazon Parrot','<image src="../images/bird2.gif">Great companion for up to 75 years');
INSERT INTO product VALUES ('AV-SB-02','BIRDS','Finch','<image src="../images/bird1.gif">Great stress reliever');

INSERT INTO supplier VALUES (1,'XYZ Pets','AC','600 Avon Way','','Los Angeles','CA','94024','212-947-0797');
INSERT INTO supplier VALUES (2,'ABC Pets','AC','700 Abalone Way','','San Francisco ','CA','94024','415-947-0797');

INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-1','FI-SW-01',16.50,10.00,1,'P','Large');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-2','FI-SW-01',16.50,10.00,1,'P','Small');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-3','FI-SW-02',18.50,12.00,1,'P','Toothless');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-4','FI-FW-01',18.50,12.00,1,'P','Spotted');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-5','FI-FW-01',18.50,12.00,1,'P','Spotless');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-6','K9-BD-01',18.50,12.00,1,'P','Male Adult');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-7','K9-BD-01',18.50,12.00,1,'P','Female Puppy');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-8','K9-PO-02',18.50,12.00,1,'P','Male Puppy');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-9','K9-DL-01',18.50,12.00,1,'P','Spotless Male Puppy');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-10','K9-DL-01',18.50,12.00,1,'P','Spotted Adult Female');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-11','RP-SN-01',18.50,12.00,1,'P','Venomless');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-12','RP-SN-01',18.50,12.00,1,'P','Rattleless');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-13','RP-LI-02',18.50,12.00,1,'P','Green Adult');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-14','FL-DSH-01',58.50,12.00,1,'P','Tailless');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-15','FL-DSH-01',23.50,12.00,1,'P','With tail');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-16','FL-DLH-02',93.50,12.00,1,'P','Adult Female');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-17','FL-DLH-02',93.50,12.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-18','AV-CB-01',193.50,92.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-19','AV-SB-02',15.50, 2.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-20','FI-FW-02',5.50, 2.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-21','FI-FW-02',5.29, 1.00,1,'P','Adult Female');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-22','K9-RT-02',135.50, 100.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-23','K9-RT-02',145.49, 100.00,1,'P','Adult Female');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-24','K9-RT-02',255.50, 92.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-25','K9-RT-02',325.29, 90.00,1,'P','Adult Female');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-26','K9-CW-01',125.50, 92.00,1,'P','Adult Male');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-27','K9-CW-01',155.29, 90.00,1,'P','Adult Female');
INSERT INTO  item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-28','K9-RT-01',155.29, 90.00,1,'P','Adult Female');

INSERT INTO inventory (itemid, qty ) VALUES ('EST-1',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-2',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-3',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-4',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-5',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-6',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-7',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-8',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-9',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-10',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-11',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-12',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-13',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-14',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-15',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-16',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-17',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-18',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-19',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-20',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-21',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-22',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-23',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-24',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-25',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-26',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-27',10000);
INSERT INTO inventory (itemid, qty ) VALUES ('EST-28',10000);

-- Product Characteristics Data (16 breeds) - 상세 정보 포함
-- FISH (4)
INSERT INTO product_characteristics VALUES ('FI-SW-01', 'LOW', 'SMALL', 'UNDER_100K', 'LOW', 'LOW', 'LOW', 'LOW', 'LOW', 10, true, true, true, true, true, '우아함, 평화로움, 관상용', '[엔젤피시] 먹이: 열대어 전용사료, 냉동 장구벌레 1일 2회. 수온: 24-28도 유지 필수. 수질: pH 6.5-7.0, 주 1회 30% 물갈이. 건강: 백점병, 지느러미썩음병 주의. 합사: 같은 크기 온순한 어종과 가능, 소형어는 잡아먹을 수 있음. 최소 수조: 60L. 초보자에게 적합하나 수온 관리는 필수.');
INSERT INTO product_characteristics VALUES ('FI-SW-02', 'HIGH', 'LARGE', 'OVER_300K', 'LOW', 'LOW', 'MEDIUM', 'LOW', 'HIGH', 20, false, false, true, false, false, '공격적, 독립적, 이국적', '[타이거샤크] 먹이: 생선, 오징어, 새우 등 육식성, 주 3-4회. 수조: 최소 5000L 이상, 강력한 여과장치 필수. 수온: 22-26도. 수질: 해수, 염도 1.020-1.025 유지. 건강: 피부병, 기생충 정기 체크. 합사: 단독 사육만 가능. 월 유지비: 전기료+먹이 50만원 이상. 전문가 외 사육 금지.');
INSERT INTO product_characteristics VALUES ('FI-FW-01', 'MEDIUM', 'LARGE', 'BETWEEN_100K_300K', 'LOW', 'LOW', 'LOW', 'MEDIUM', 'LOW', 25, false, false, true, true, true, '온순함, 사교적, 장수', '[잉어/비단잉어] 먹이: 잉어 전용사료, 빵, 과일 1일 2-3회. 연못: 최소 1000L, 깊이 60cm 이상. 수온: 2-30도 적응력 강함. 수질: pH 7.0-8.0, 여과장치 필수. 건강: 잉어 헤르페스바이러스 주의, 봄가을 기생충 체크. 합사: 다른 잉어와 가능. 겨울: 연못 바닥에서 동면, 먹이 중단.');
INSERT INTO product_characteristics VALUES ('FI-FW-02', 'LOW', 'SMALL', 'UNDER_100K', 'LOW', 'LOW', 'LOW', 'LOW', 'LOW', 10, true, true, true, true, true, '온순함, 친근함, 초보자 친화', '[금붕어] 먹이: 금붕어 사료, 1일 1-2회 소량. 수조: 최소 20L, 클수록 좋음. 수온: 18-24도, 상온 가능. 수질: pH 7.0-7.5, 주 1회 30% 물갈이. 건강: 백점병, 부레병 주의, 과식 금지. 합사: 같은 금붕어끼리만. 산소공급: 에어펌프 권장. 가장 키우기 쉬운 관상어, 아이 첫 반려동물로 추천.');

-- DOGS (6)
INSERT INTO product_characteristics VALUES ('K9-BD-01', 'MEDIUM', 'MEDIUM', 'BETWEEN_100K_300K', 'MEDIUM', 'LOW', 'LOW', 'HIGH', 'MEDIUM', 10, false, true, true, false, true, '느긋함, 애정적, 고집스러움', '[불독] 먹이: 고품질 건식사료 1일 2회, 체중관리 중요(비만 주의). 운동: 하루 20-30분 가벼운 산책, 격한 운동 금지. 건강: 호흡기질환, 피부주름 감염, 고관절이형성 주의. 더위에 매우 약해 여름철 에어컨 필수. 그루밍: 주 1회 빗질, 주름 사이 매일 닦기. 훈련: 고집있어 인내심 필요. 분리불안 있어 장시간 혼자두기 금지.');
INSERT INTO product_characteristics VALUES ('K9-PO-02', 'HIGH', 'MEDIUM', 'OVER_300K', 'LOW', 'MEDIUM', 'HIGH', 'HIGH', 'LOW', 14, false, true, true, false, true, '영리함, 활발함, 사교적', '[푸들] 먹이: 고단백 사료 1일 2회. 운동: 하루 1시간 이상 산책+놀이 필수. 건강: 귀감염, 치아문제, 슬개골탈구 주의. 그루밍: 4-6주마다 전문 미용 필수(월 5-10만원), 매일 빗질. 털빠짐 거의 없어 알레르기 환자에게 적합. 훈련: 지능 최상위, 트릭 학습 빠름. 사회화 필수, 분리불안 방지 훈련 필요.');
INSERT INTO product_characteristics VALUES ('K9-DL-01', 'HIGH', 'LARGE', 'BETWEEN_100K_300K', 'HIGH', 'MEDIUM', 'HIGH', 'HIGH', 'MEDIUM', 12, false, false, true, false, true, '활발함, 충성스러움, 에너지 넘침', '[달마시안] 먹이: 고단백 대형견 사료 1일 2회, 요산결석 예방식 권장. 운동: 하루 2시간 이상 격렬한 운동 필수, 달리기/자전거 동반. 건강: 선천성 난청(30%), 요로결석, 피부알레르기 주의. 그루밍: 주 2-3회 빗질, 털 많이 빠짐. 훈련: 에너지 발산 안되면 파괴적 행동. 넓은 마당 필수.');
INSERT INTO product_characteristics VALUES ('K9-RT-01', 'MEDIUM', 'LARGE', 'BETWEEN_100K_300K', 'HIGH', 'LOW', 'HIGH', 'HIGH', 'LOW', 12, false, false, true, false, true, '친절함, 충성스러움, 온순함', '[골든리트리버] 먹이: 대형견 사료 1일 2회, 비만 주의 체중관리. 운동: 하루 1-2시간 산책, 수영 매우 좋아함. 건강: 고관절이형성, 암 발생률 높음, 심장병 주의. 그루밍: 매일 빗질 필수, 털 엄청 빠짐, 환절기 특히 심함. 훈련: 최고의 가족견, 아이들과 안전, 처음 키우는 분께 추천. 입질 습관 교정 필요.');
INSERT INTO product_characteristics VALUES ('K9-RT-02', 'MEDIUM', 'LARGE', 'BETWEEN_100K_300K', 'HIGH', 'LOW', 'HIGH', 'HIGH', 'LOW', 12, false, false, true, false, true, '활발함, 우호적, 열정적', '[래브라도리트리버] 먹이: 대형견 사료 1일 2회, 식탐 강해 비만 주의. 운동: 하루 1-2시간 산책+수영, 물 매우 좋아함. 건강: 고관절이형성, 비만, 귀감염 주의. 그루밍: 주 2회 빗질, 털 많이 빠짐. 훈련: 안내견/치료견으로 활용될 만큼 훈련성 최상. 입에 물건 무는 습관, 장난감 필수. 사교적이라 모든 사람/동물과 친함.');
INSERT INTO product_characteristics VALUES ('K9-CW-01', 'LOW', 'SMALL', 'UNDER_100K', 'LOW', 'HIGH', 'MEDIUM', 'HIGH', 'MEDIUM', 16, true, true, true, false, false, '용감함, 경계심, 충성스러움', '[치와와] 먹이: 소형견 사료 1일 2-3회 소량씩, 저혈당 주의. 운동: 하루 20-30분 가벼운 산책. 건강: 슬개골탈구, 치아문제, 저혈당, 두개골 열림 주의. 그루밍: 주 1회 빗질(단모), 추위에 약해 겨울 옷 필수. 훈련: 경계심 강해 과도한 짖음 교정 필요. 소형이라 아이들에게 다칠 수 있어 성인가정 권장. 1인 가구 적합.');


-- REPTILES (2)
INSERT INTO product_characteristics VALUES ('RP-SN-01', 'HIGH', 'MEDIUM', 'BETWEEN_100K_300K', 'LOW', 'LOW', 'LOW', 'LOW', 'HIGH', 20, false, false, true, true, false, '독립적, 신비로움, 위험성 있음', '[방울뱀] 먹이: 냉동 쥐/햄스터 주 1회. 사육장: 최소 90x45cm, 탈출방지 잠금장치 필수. 온도: 낮 28-32도, 밤 24-26도, 핫스팟 필수. 습도: 40-60%. 건강: 호흡기감염, 진드기 주의. 독이 있어 응급처치 키트 필수, 가까운 병원 위치 파악. 법적 규제 확인 필요. 어린이 있는 가정 절대 금지. 파충류 전문가만 사육 권장.');
INSERT INTO product_characteristics VALUES ('RP-LI-02', 'MEDIUM', 'LARGE', 'BETWEEN_100K_300K', 'LOW', 'LOW', 'LOW', 'LOW', 'MEDIUM', 20, false, false, true, true, false, '온순함, 독립적, 이국적', '[이구아나] 먹이: 채소(청경채, 콜라드), 과일 1일 1회, 초식성. 사육장: 성체 최소 180x90x120cm, 나뭇가지/선반 필수. 온도: 낮 29-35도, 밤 24도, UV-B 조명 10-12시간. 습도: 70-80%, 분무 필수. 건강: 대사성골질환, 신장병 주의. 핸들링: 어릴 때부터 매일 해야 길듬. 성체 1.5m까지 자람, 공간 충분히 확보.');

-- CATS (2)
INSERT INTO product_characteristics VALUES ('FL-DSH-01', 'LOW', 'SMALL', 'UNDER_100K', 'MEDIUM', 'LOW', 'MEDIUM', 'MEDIUM', 'LOW', 14, true, true, true, true, true, '사냥 본능, 충성스러움, 장난기', '[맹크스] 먹이: 고양이 사료 1일 2회, 체중관리. 운동: 사냥놀이 좋아함, 낚싯대 장난감 추천. 건강: 맹크스 증후군(척추문제) 주의, 변비 가능성. 그루밍: 주 1-2회 빗질(단모/장모 따라 다름). 특징: 꼬리 없음/짧음, 뒷다리 길어 토끼처럼 뜀. 개처럼 주인 따라다님, 물건 물어오기 가능. 혼자 있는 시간 잘 견딤, 직장인 적합.');
INSERT INTO product_characteristics VALUES ('FL-DLH-02', 'HIGH', 'SMALL', 'BETWEEN_100K_300K', 'HIGH', 'LOW', 'LOW', 'MEDIUM', 'LOW', 15, true, true, true, false, true, '우아함, 온순함, 조용함', '[페르시안] 먹이: 장모종 전용 사료 1일 2회, 헤어볼 관리. 운동: 활동량 적음, 조용히 쉬는 것 선호. 건강: 다낭성신장병, 호흡기문제(납작 얼굴), 눈물흘림 주의. 그루밍: 매일 빗질 필수! 엉킴 방지, 월 1회 목욕. 눈/코 주름 매일 닦기. 털 많이 날림, 알레르기 있으면 부적합. 조용하고 얌전해 아파트 적합, 아이들과도 잘 지냄.');

-- BIRDS (2)
INSERT INTO product_characteristics VALUES ('AV-CB-01', 'HIGH', 'MEDIUM', 'BETWEEN_100K_300K', 'MEDIUM', 'HIGH', 'HIGH', 'HIGH', 'MEDIUM', 50, false, false, true, false, true, '지능적, 수다스러움, 애정적', '[아마존앵무] 먹이: 펠릿 70%, 과일/채소 30%, 씨앗은 간식으로만. 새장: 최소 90x60x120cm, 매일 새장 밖 활동 2-4시간. 건강: 깃털뽑기, 비만, 간질환 주의. 수명: 40-70년! 평생 책임질 각오 필요, 유언장에 포함 고려. 소음: 매우 시끄러움, 아파트 민원 가능성. 훈련: 말 50-100단어 학습, 지능 5세 아이 수준. 매일 교감 필수, 방치시 문제행동.');
INSERT INTO product_characteristics VALUES ('AV-SB-02', 'LOW', 'SMALL', 'UNDER_100K', 'LOW', 'LOW', 'MEDIUM', 'LOW', 'LOW', 8, true, true, true, true, true, '평화로움, 독립적, 아름다운 노래', '[핀치/십자매] 먹이: 핀치 전용 씨앗, 채소 보충 1일 1회. 새장: 최소 45x30x45cm, 가로로 긴 것 선호. 건강: 호흡기감염, 진드기 주의, 외풍 금지. 그루밍: 스스로 관리, 목욕물 제공. 소음: 조용한 지저귐, 이웃 민원 없음. 특징: 손에 앉히기 어려움, 관상용 추천. 2마리 이상 함께 키우면 좋음. 바쁜 직장인, 조용한 반려동물 원하는 분께 최적.');

