# TechStore DevOps Proje Raporu

## 1. Proje Bilgileri

- Öğrenci: Engin Elibol
- Numara: 2310238554
- Ders: Yazılım Kalitesi ve Güvenliği
- Repo Linki: `https://github.com/Sqap85/yazilim-kalitesi-ve-guvenligi-projesi`

## 2. Proje Özeti

Bu projede Flask tabanlı bir e-ticaret uygulaması için temel DevOps hattı hazırlandı. Uygulama birim testleriyle doğrulandı, Docker ile paketlendi, Jenkins pipeline dosyası düzenlendi, SonarQube ve Prometheus/Grafana yapılandırmaları teslime uygun hale getirildi.

Yerel doğrulama sonucu:

- Birim test sayısı: `34`
- UI test sayısı: `10`
- Hedef coverage: `%80+`
- Ölçülen coverage: `%93`
- UI test sonucu: `10/10 geçti`
- Uygulama servis endpoint'leri: `/`, `/product/<id>`, `/cart`, `/checkout`, `/health`, `/metrics`

## 3. Adımlarda Karşılaşılan Zorluklar ve Çözümler

### Adım 1: Proje yapısının hazırlanması

Sorun:
Arşivden çıkan proje klasörü doğrudan çalıştırılabilir görünse de test çalıştırma sırasında modül yolu kaynaklı hata oluştu.

Çözüm:
`tests/conftest.py` eklenerek proje kök dizini test ortamına güvenli biçimde tanıtıldı.

### Adım 2: Yerel çalıştırma ve bağımlılıklar

Sorun:
Rehberde `pytest-cov` ve `webdriver-manager` kullanılıyor fakat bunlar bağımlılık listesinde yer almıyordu.

Çözüm:
`requirements.txt` güncellendi ve eksik geliştirme/test bağımlılıkları eklendi.

### Adım 3: Testlerin güvenilirliği

Sorun:
Sepete ekleme ve güncelleme akışlarında geçersiz adet, kümülatif stok aşımı ve hatalı JSON gibi durumlar eksik doğrulanıyordu.

Çözüm:
`app.py` içinde istek doğrulama katmanı güçlendirildi, ek testlerle bu davranışlar güvence altına alındı.

### Adım 4: Jenkins pipeline güvenilirliği

Sorun:
Pipeline içinde UI testleri başarısız olsa bile stage başarılı görünebiliyordu. Ayrıca deploy aşaması sabit Docker Hub kullanıcı adı nedeniyle kırılabilirdi.

Çözüm:
`Jenkinsfile` güncellenerek UI testlerinde hata gizleme kaldırıldı, deploy aşaması yerel oluşturulan imajı kullanacak şekilde düzeltildi.

## 4. CI/CD Pipeline'ın Kaliteye Katkısı

Pipeline aşağıdaki hataları erken aşamada yakalayacak şekilde tasarlandı:

- Kodun test ortamında import edilememesi
- Sepet ve checkout akışındaki iş mantığı hataları
- Coverage düşüşleri
- SonarQube kalite kapısı ihlalleri
- Docker build veya deploy kaynaklı servis erişim problemleri
- `/health` smoke test başarısızlıkları

Bu sayede sorunların üretim öncesi görünür olması sağlanır ve manuel kontrol yükü azalır.

## 5. Docker ile Containerization

Uygulama Dockerfile kullanılarak container image haline getirildi ve servisler `docker compose` ile ayağa kaldırıldı.

Yerel doğrulama:

- Uygulama image adı: `techstore-devops-app:latest`
- Jenkins image adı: `techstore-jenkins-local:latest`
- Compose servisleri: `prometheus`, `grafana`, `sonarqube`
- Uygulama sağlık kontrolü: `{"service":"techstore","status":"healthy","version":"1.0.0"}`

Kullanılan temel komutlar:

```bash
docker build -t techstore-devops-app:latest .
docker compose up -d
docker compose ps
docker ps
```

Not:
Yerel makinede `5000` portu macOS AirPlay servisiyle çakıştığı için host tarafındaki uygulama kontrolleri `5001` portunda, container ağı içindeki kontroller ise `5000` portunda doğrulandı.

Docker ve Docker Compose doğrulama ekran görüntüsü:

![Docker Durumu](screenshots/docker-status.png)

## 6. SonarQube Bulguları ve İyileştirmeler

Yerel kod incelemesinde aşağıdaki iyileştirmeler yapıldı:

- Giriş doğrulama eksikleri giderildi
- Rastgele kategori sıralaması deterministik hale getirildi
- Hata gizleyen pipeline davranışı kaldırıldı
- Test sayısı artırılarak kritik akışlar daha iyi kapsandı

SonarQube doğrulama sonucu:

- Proje: `techstore`
- Quality Gate Durumu: `PASSED`

SonarQube ekran görüntüsü:

![SonarQube Dashboard](screenshots/sonarqube.png)

## 7. Grafana Dashboard

Önerilen paneller:

1. `rate(http_requests_total[1m])`
2. `histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))`
3. `topk(5, cart_add_total)`
4. `rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m])`

Yerel dashboard doğrulaması:

- Dashboard adı: `TechStore Overview`
- Prometheus target durumu: `techstore = UP`

Prometheus ekran görüntüsü:

![Prometheus Targets](screenshots/prometheus.png)

Grafana ekran görüntüsü:

![Grafana Dashboard](screenshots/grafana.png)

## 8. Jenkins Build Ekran Görüntüsü

Yerel Jenkins doğrulaması:

- Job adı: `techstore-pipeline`
- Başarılı build: `#3`
- Son durum: `SUCCESS`

![Jenkins Build Success](screenshots/jenkins.png)

## 9. Coverage Raporu

- Komut: `pytest tests/test_app.py -v --cov=app --cov-report=term-missing`
- Sonuç: `34 passed`, `TOTAL %93`

![Coverage Raporu](screenshots/coverage.png)

## 10. UI Test Sonucu

- Komut: `BASE_URL=http://127.0.0.1:5001 pytest tests/test_ui.py -v`
- Sonuç: `10/10 test geçti`

Uygulama ekran görüntüleri:

![Ana Sayfa](screenshots/home.png)
![Urun Detay](screenshots/product.png)
![Sepet](screenshots/cart.png)
![Checkout](screenshots/checkout.png)
![Health Endpoint](screenshots/health.png)

