INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (1, 'CompanyRegistration', 'lau', 'ES', null, null);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (2, 'CompanyRegistration', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (1, 'SI031', 'Mura', 'iso6523-actorid-upis::9991:SI990000105', 'Vlada Mure', 2);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (1, 'usip', 'https://moai.gov.si/usip', 1);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (3, 'CompanyRegistration', 'nuts0', 'LU', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (2, 'LU', 'LUXEMBOURG', 'iso6523-actorid-upis::9991:LU000000025', 'CENTRE DES TECHNOLOGIES DE L''INFORMATION DE L''ETAT''', 3);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (2, 'ip', null, 2);

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (4, 'CompanyRegistration', 'nuts0', 'SI', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (3, 'SI', 'SLOVENIJA', 'iso6523-actorid-upis::9991:SI990000105', 'Minister za notranje zadeve', 4);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (3, 'ip', null, 3);
INSERT INTO Param (id, title, id_provision) VALUES (1, 'SI/nuts3', 3);
INSERT INTO ParamsSet (id, id_param, paramValue) VALUES (1, 1, 'SI/SI031');
INSERT INTO ParamsSet (id, id_param, paramValue) VALUES (2, 1, 'SI/SI034');

INSERT INTO Source (id, canonicalEvidenceTypeId, atulevel, countryCode, numProvisions, organisation) VALUES (5, 'BirthCertificate', 'nuts0', 'ES', null, null);
INSERT INTO ProvisionItem (id, atuCode, AtuLatinName, dataOwnerId, DataOwnerPrefLabel, id_source) VALUES (4, 'ES', 'España', 'iso6523-actorid-upis::9921:ESS2833002E', 'Ministerio de Justicia', 5);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (4, 'ip', null , 4);
INSERT INTO Provision (id, provisionType, redirectURL, id_provisionItem) VALUES (2, 'usip', 'https://ctie.lu/usip', 4);
