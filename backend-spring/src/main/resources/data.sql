-- Usuários de teste (senhas em texto puro, só para fins acadêmicos)
INSERT INTO usuarios (nome, email, senha) VALUES ('Admin RedZone', 'admin@redzone.com', 'admin123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Lucas', 'lucas@email.com', 'lucas123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Mariana', 'mariana@email.com', 'mariana123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Rafael', 'rafael@email.com', 'rafael123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Sofia', 'sofia@email.com', 'sofia123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Tiago', 'tiago@email.com', 'tiago123');

-- Produtos: 3 por categoria (as imagens ficam em public/img/produtos, no front-end)
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Headset Gamer', 199.9, '/img/produtos/headset.jpg', 'Áudio', 'Headset com som surround.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Microfone Condensador USB', 259.9, '/img/produtos/microfone-usb.jpg', 'Áudio', 'Microfone USB com captação nítida, ideal para streams e chamadas.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Caixa de Som Bluetooth RGB', 189.9, '/img/produtos/caixa-de-som.jpg', 'Áudio', 'Caixa de som portátil com graves potentes e iluminação RGB.');

INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Cadeira Gamer Ergonômica', 1299.9, '/img/produtos/cadeira-gamer.jpg', 'Móveis', 'Cadeira reclinável com apoio lombar e braços ajustáveis.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Mesa Gamer com LED', 899.9, '/img/produtos/mesa-gamer.jpg', 'Móveis', 'Mesa ampla com superfície em fibra de carbono e iluminação LED.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Estante Organizadora para Setup', 459.9, '/img/produtos/estante.jpg', 'Móveis', 'Estante com prateleiras para monitor, console e acessórios.');

INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Placa de Vídeo RTX 4060 8GB', 2499.9, '/img/produtos/placa-video.jpg', 'Hardware', 'Placa de vídeo com 8GB para jogar em Full HD com alto desempenho.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Processador Ryzen 5 5600', 899.9, '/img/produtos/processador.jpg', 'Hardware', 'Processador de 6 núcleos e 12 threads, ótimo custo-benefício.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('SSD NVMe 1TB', 379.9, '/img/produtos/ssd.jpg', 'Hardware', 'SSD ultrarrápido para carregar sistema e jogos em segundos.');

INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Mouse Gamer RGB', 129.9, '/img/produtos/mouse-gamer.jpg', 'Acessórios', 'Mouse com sensor de alta precisão e DPI ajustável.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Teclado Mecânico RGB', 349.9, '/img/produtos/teclado.jpg', 'Acessórios', 'Teclado mecânico com switches responsivos e iluminação por tecla.');
INSERT INTO produtos (nome, preco, imagem, categoria, descricao) VALUES ('Mousepad Speed Grande', 79.9, '/img/produtos/mousepad.jpg', 'Acessórios', 'Mousepad extra grande com base antiderrapante e superfície speed.');
