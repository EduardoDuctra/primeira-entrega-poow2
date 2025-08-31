CREATE TABLE usuario (
                         cod_usuario SERIAL PRIMARY KEY,
                         uuid_usuario UUID DEFAULT gen_random_uuid(),
                         nome VARCHAR(50) NOT NULL,
                         email VARCHAR(50) not null UNIQUE,
                         senha VARCHAR(14) NOT NULL,
                         sexo CHAR(1) NOT NULL,
                         altura DOUBLE PRECISION
);
CREATE TABLE dados (
                       cod_dado SERIAL PRIMARY KEY,
                       uuid_dado UUID DEFAULT gen_random_uuid(),
                       peso DOUBLE PRECISION,
                       glicose INT,
                       colesterol_hdl INT,
                       colesterol_vldl INT,
                       creatina INT,
                       trigliceridio INT,
                       cod_usuario INT REFERENCES usuario(cod_usuario)
);

CREATE TABLE relatorio (
                        cod_usuario INT REFERENCES usuario(cod_usuario),
                        cod_dado INT REFERENCES dados(cod_dado),
                        data DATE NOT NULL,
                        PRIMARY KEY (cod_usuario, cod_dado, data)
);

CREATE TABLE medicamento (
                            cod_medicamento SERIAL PRIMARY KEY,
                            uuid_medicamento UUID DEFAULT gen_random_uuid(),
                             nome_medicamento VARCHAR(50) NOT NULL,
                             duracao_tratamento INT,
                             data_inicio DATE,
                             dose_diaria INT,
                             cod_usuario INT REFERENCES usuario(cod_usuario)
);