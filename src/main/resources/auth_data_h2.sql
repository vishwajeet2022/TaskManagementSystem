INSERT INTO authority (authority_id, authority_code) 
SELECT NEXT VALUE FOR authority_seq, 'CREATE_TASK';

INSERT INTO authority (authority_id, authority_code) 
SELECT NEXT VALUE FOR authority_seq, 'UPDATE_TASK';

INSERT INTO authority (authority_id, authority_code) 
SELECT NEXT VALUE FOR authority_seq, 'DELETE_TASK';

INSERT INTO authority (authority_id, authority_code) 
SELECT NEXT VALUE FOR authority_seq, 'READ_TASK';