-- Usuários de teste (senhas em texto puro, só para fins acadêmicos)
INSERT INTO usuarios (nome, email, senha) VALUES ('Admin RedZone', 'admin@redzone.com', 'admin123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Lucas', 'lucas@email.com', 'lucas123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Mariana', 'mariana@email.com', 'mariana123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Rafael', 'rafael@email.com', 'rafael123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Sofia', 'sofia@email.com', 'sofia123');
INSERT INTO usuarios (nome, email, senha) VALUES ('Tiago', 'tiago@email.com', 'tiago123');

-- Produtos: 3 por categoria (as imagens ficam em public/img/produtos, no front-end)
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Headset Gamer', 199.9, '/img/produtos/headset.jpg', 'Áudio', 'Headset com som surround.', 15);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Microfone Condensador USB', 259.9, '/img/produtos/microfone-usb.jpg', 'Áudio', 'Microfone USB com captação nítida, ideal para streams e chamadas.', 8);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Caixa de Som Bluetooth RGB', 189.9, '/img/produtos/caixa-de-som.jpg', 'Áudio', 'Caixa de som portátil com graves potentes e iluminação RGB.', 3);

INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Cadeira Gamer Ergonômica', 1299.9, '/img/produtos/cadeira-gamer.jpg', 'Móveis', 'Cadeira reclinável com apoio lombar e braços ajustáveis.', 5);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Mesa Gamer com LED', 899.9, '/img/produtos/mesa-gamer.jpg', 'Móveis', 'Mesa ampla com superfície em fibra de carbono e iluminação LED.', 0);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Estante Organizadora para Setup', 459.9, '/img/produtos/estante.jpg', 'Móveis', 'Estante com prateleiras para monitor, console e acessórios.', 12);

INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Placa de Vídeo RTX 4060 8GB', 2499.9, '/img/produtos/placa-video.jpg', 'Hardware', 'Placa de vídeo com 8GB para jogar em Full HD com alto desempenho.', 4);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Processador Ryzen 5 5600', 899.9, '/img/produtos/processador.jpg', 'Hardware', 'Processador de 6 núcleos e 12 threads, ótimo custo-benefício.', 10);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('SSD NVMe 1TB', 379.9, '/img/produtos/ssd.jpg', 'Hardware', 'SSD ultrarrápido para carregar sistema e jogos em segundos.', 25);

INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Mouse Gamer RGB', 129.9, '/img/produtos/mouse-gamer.jpg', 'Acessórios', 'Mouse com sensor de alta precisão e DPI ajustável.', 30);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Teclado Mecânico RGB', 349.9, '/img/produtos/teclado.jpg', 'Acessórios', 'Teclado mecânico com switches responsivos e iluminação por tecla.', 18);
INSERT INTO produtos (nome, preco, imagem, categoria, descricao, estoque) VALUES ('Mousepad Speed Grande', 79.9, '/img/produtos/mousepad.jpg', 'Acessórios', 'Mousepad extra grande com base antiderrapante e superfície speed.', 40);

-- Detalhes opcionais (marca, garantia e especificações) só em ALGUNS produtos:
-- os outros ficam com esses campos nulos, e a tela de detalhe simplesmente não mostra essas seções.
-- Nas especificações, cada linha é um item da ficha técnica (CHAR(10) = quebra de linha).
UPDATE produtos SET marca = 'HyperSound', garantia = '12 meses',
  especificacoes = 'Conexão: USB' || CHAR(10) || 'Padrão polar: cardioide' || CHAR(10) || 'Taxa de amostragem: 48 kHz' || CHAR(10) || 'Acompanha: tripé e filtro pop'
  WHERE nome = 'Microfone Condensador USB';
UPDATE produtos SET marca = 'ErgoPlay', garantia = '24 meses',
  especificacoes = 'Peso suportado: até 120 kg' || CHAR(10) || 'Reclinação: 90° a 180°' || CHAR(10) || 'Revestimento: couro sintético' || CHAR(10) || 'Braços: 4D ajustáveis'
  WHERE nome = 'Cadeira Gamer Ergonômica';
UPDATE produtos SET marca = 'NVIDIA', garantia = '36 meses',
  especificacoes = 'Memória: 8 GB GDDR6' || CHAR(10) || 'Interface: PCI Express 4.0' || CHAR(10) || 'Saídas: 3x DisplayPort, 1x HDMI' || CHAR(10) || 'Fonte recomendada: 550 W'
  WHERE nome = 'Placa de Vídeo RTX 4060 8GB';
UPDATE produtos SET marca = 'AMD', garantia = '12 meses',
  especificacoes = 'Núcleos / threads: 6 / 12' || CHAR(10) || 'Clock: 3.5 GHz (até 4.4 GHz)' || CHAR(10) || 'Soquete: AM4'
  WHERE nome = 'Processador Ryzen 5 5600';
UPDATE produtos SET marca = 'KeyForce', garantia = '12 meses',
  especificacoes = 'Switch: mecânico red (linear)' || CHAR(10) || 'Layout: ABNT2' || CHAR(10) || 'Iluminação: RGB por tecla'
  WHERE nome = 'Teclado Mecânico RGB';
-- Só marca, sem garantia nem especificações (para mostrar que cada campo é independente)
UPDATE produtos SET marca = 'ClickPro' WHERE nome = 'Mouse Gamer RGB';

-- Promoções: só quem tem preco_promocional preenchido aparece em /promocoes (GET /produtos?promocao=true)
UPDATE produtos SET preco_promocional = 219.9 WHERE nome = 'Microfone Condensador USB';
UPDATE produtos SET preco_promocional = 999.9 WHERE nome = 'Cadeira Gamer Ergonômica';
UPDATE produtos SET preco_promocional = 329.9 WHERE nome = 'SSD NVMe 1TB';
UPDATE produtos SET preco_promocional = 279.9 WHERE nome = 'Teclado Mecânico RGB';
