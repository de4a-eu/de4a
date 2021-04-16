-- ========= INITIAL DATA TO PROVIDE PILOT REQUIREMENTS ========= --
INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (1, 'CompanyRegistration', 'nuts0', 'AT', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (1, 'AT', '�STERREICH', 'iso6523-actorid-upis::9991:AT000000271', 'BUNDESMINISTERIUM FUER DIGITALISIERUNG UND WIRTSCHAFTSSTANDORT (BMDW)', 1);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (1, 'ip', null , 1);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (2, 'CompanyRegistration', 'nuts0', 'NL', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (2, 'NL', 'NEDERLAND', 'iso6523-actorid-upis::9991:NL990000106', 'Chamber of Commerce of Netherlands (KVK)', 2);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (2, 'ip', null , 2);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (3, 'CompanyRegistration', 'nuts0', 'SE', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (3, 'SE', 'SVERIGE', 'iso6523-actorid-upis::9991:SE000000013', 'Rijksdienst voor Ondernemend Nederland (BVE)', 3);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (3, 'ip', null , 3);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (4, 'CompanyRegistration', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (4, 'RO', 'ROM�NIA', 'iso6523-actorid-upis::9991:RO000000006', 'OFICIUL NATIONAL AL REGISTRULUI COMERTULUI (ONRC)', 4);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (4, 'ip', null , 4);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (5, 'HigherEdCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (5, 'ES', 'Espa�a', 'iso6523-actorid-upis::9921:E05025101', 'MINISTERIO DE UNIVERSIDADES', 5);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (5, 'ip', null , 5);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (6, 'HigherEdCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (6, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI000000016', 'MINISTRSTVO ZA IZOBRAZEVANJE, ZNANOST IN SPORT (MIZS)', 6);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (6, 'ip', null , 6);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (7, 'HigherEdCertificate', 'nuts0', 'PT', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (7, 'PT', 'PORTUGAL', 'iso6523-actorid-upis::9991:PT990000101', 'Instituto Superior T�cnico, Universidade de Lisboa', 7);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (7, 'ip', null , 7);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (8, 'BirthCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (8, 'ES', 'Espa�a', 'iso6523-actorid-upis::9921:E00003901', 'Ministerio de Justicia', 8);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (8, 'ip', null , 8);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (9, 'BirthCertificate', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (9, 'RO', 'ROM�NIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 9);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (9, 'ip', null , 9);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (10, 'BirthCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (10, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 10);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (10, 'ip', null , 10);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (11, 'BirthCertificate', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (11, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 11);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (11, 'ip', null , 11);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (12, 'ResidencyProof', 'nuts0', 'PT', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (12, 'PT', 'PORTUGAL', 'iso6523-actorid-upis::9991:PT000000026', 'AGENCIA PARA A MODERNIZACAO ADMINISTRATIVA IP', 12);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (12, 'ip', null , 12);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (13, 'DeathCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (13, 'ES', 'Espa�a', 'iso6523-actorid-upis::9921:E00003901', 'Ministerio de Justicia', 13);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (13, 'ip', null , 13);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (14, 'DeathCertificate', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (14, 'RO', 'ROM�NIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 14);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (14, 'ip', null , 14);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (15, 'DeathCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (15, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 15);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (15, 'ip', null , 15);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (16, 'DeathCertificate', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (16, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 16);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (16, 'ip', null , 16);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (17, 'MarriageCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (17, 'ES', 'Espa�a', 'iso6523-actorid-upis::9921:E00003901', 'Ministerio de Justicia', 17);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (17, 'ip', null , 17);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (18, 'MarriageCertificate', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (18, 'RO', 'ROM�NIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 18);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (18, 'ip', null , 18);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (19, 'MarriageCertificate', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (19, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 19);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (19, 'ip', null , 19);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (20, 'MarriageCertificate', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (20, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 20);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (20, 'ip', null , 20);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (21, 'ResidencyProof', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (21, 'ES', 'Espa�a', 'iso6523-actorid-upis::9921:EA0042823', 'Instituto Nacional de Estad�stica (INE)', 21);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (21, 'ip', null , 21);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (22, 'ResidencyProof', 'nuts0', 'RO', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (22, 'RO', 'ROM�NIA', 'iso6523-actorid-upis::9991:RO000000005', 'MINISTERUL AFACERILOR INTERNE (MoAI)', 22);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (22, 'ip', null , 22);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (23, 'ResidencyProof', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (23, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Ministrstvo za notranje zadeve', 23);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (23, 'ip', null , 23);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (24, 'ResidencyProof', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (24, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L'INFORMATION DE L'ETAT (CTIE)', 24);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (24, 'ip', null , 24);