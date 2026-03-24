INSERT INTO categories (id, name)
SELECT gen_random_uuid(), name
FROM (VALUES
          ('Alimentação'),
          ('Energia'),
          ('Água'),
          ('Aluguel'),
          ('Internet'),
          ('Transporte'),
          ('Lazer'),
          ('Saúde'),
          ('Outros')
     ) AS v(name)
    ON CONFLICT (name) DO NOTHING;