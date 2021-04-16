-- ========= INITIAL DATA TO PROVIDE PILOT REQUIREMENTS ========= --
INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (6, 'CompanyRegistration', 'nuts0', 'AT', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (5, 'AT', 'ÖSTERREICH', 'iso6523-actorid-upis::9991:AT000000271', 'BUNDESMINISTERIUM FUER DIGITALISIERUNG UND WIRTSCHAFTSSTANDORT (BMDW)', 6);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (5, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (7, 'CompanyRegistration', 'nuts0', 'NL', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (6, 'NL', 'NEDERLAND', 'iso6523-actorid-upis::9991:NL990000106', 'Chamber of Commerce of Netherlands (KVK)', 7);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (6, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (8, 'CompanyRegistration', 'nuts0', 'SE', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (7, 'SE', 'SVERIGE', 'iso6523-actorid-upis::9991:SE000000013', 'Rijksdienst voor Ondernemend Nederland (BVE)', 8);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (7, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (9, 'CompanyRegistration', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (8, 'RO', 'ROMÂNIA', 'iso6523-actorid-upis::9991:RO000000006', 'OFICIUL NATIONAL AL REGISTRULUI COMERTULUI (ONRC)', 9);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (8, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (10, 'HigherEdCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (9, 'ES', 'España', 'iso6523-actorid-upis::9921:E05025101', 'MINISTERIO DE UNIVERSIDADES', 10);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (9, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (11, 'HigherEdCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (10, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI000000016', 'MINISTRSTVO ZA IZOBRAZEVANJE, ZNANOST IN SPORT (MIZS)', 11);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (10, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (12, 'HigherEdCertificate', 'nuts0', 'PT', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (11, 'PT', 'PORTUGAL', 'iso6523-actorid-upis::9991:PT990000101', 'Instituto Superior Técnico, Universidade de Lisboa', 12);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (11, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (13, 'BirthCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (12, 'ES', 'España', 'iso6523-actorid-upis::9921:E00003901', 'Ministerio de Justicia', 13);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (12, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (14, 'BirthCertificate', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (13, 'RO', 'ROMÂNIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 14);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (13, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (15, 'BirthCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (14, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 15);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (14, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (16, 'BirthCertificate', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (15, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 16);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (15, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (17, 'ResidencyProof', 'nuts0', 'PT', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (16, 'PT', 'PORTUGAL', 'iso6523-actorid-upis::9991:PT000000026', 'AGENCIA PARA A MODERNIZACAO ADMINISTRATIVA IP', 17);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (16, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (18, 'DeathCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (17, 'ES', 'España', 'iso6523-actorid-upis::9921:E00003901', 'Ministerio de Justicia', 18);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (17, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (19, 'DeathCertificate', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (18, 'RO', 'ROMÂNIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 19);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (18, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (20, 'DeathCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (19, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 20);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (19, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (21, 'DeathCertificate', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (20, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 21);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (20, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (22, 'MarriageCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (21, 'ES', 'España', 'iso6523-actorid-upis::9921:E00003901', 'Ministerio de Justicia', 22);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (21, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (23, 'MarriageCertificate', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (22, 'RO', 'ROMÂNIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 23);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (22, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (24, 'MarriageCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (23, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 24);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (23, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (25, 'MarriageCertificate', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (24, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 25);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (24, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (26, 'ResidencyProof', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (25, 'ES', 'España', 'iso6523-actorid-upis::9921:EA0042823', 'Instituto Nacional de Estadística (INE)', 26);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (25, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (27, 'ResidencyProof', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (26, 'RO', 'ROMÂNIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 27);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (26, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (28, 'ResidencyProof', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (27, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 28);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (27, 'im', null , {provisionItemId});

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (29, 'ResidencyProof', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (28, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 29);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (28, 'im', null , {provisionItemId});