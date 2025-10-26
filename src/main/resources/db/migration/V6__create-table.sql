CREATE TABLE nome_medicamento (
                                  cod_nome_medicamento SERIAL PRIMARY KEY,
                                  nome VARCHAR(50) NOT NULL UNIQUE
);


INSERT INTO nome_medicamento (nome) VALUES
                                        ('Pantoprazol'),
                                        ('Dipirona'),
                                        ('Tylenol'),
                                        ('Loratadina');


ALTER TABLE medicamento
DROP COLUMN nome_medicamento;

ALTER TABLE medicamento
ADD COLUMN cod_nome_medicamento INT REFERENCES nome_medicamento(cod_nome_medicamento);