Comando: Generar un almacén de claves PKCS12 con una clave privada y su certificado
sh
Copiar
Editar
keytool -genkeypair -alias servidor -keyalg RSA -keysize 2048 -validity 365 \
-keystore servidor_keystore.p12 -storetype PKCS12 -storepass 1234567
✔ Este comando genera un par de claves RSA (clave pública y privada) y un certificado, y los almacena en un archivo PKCS12 (.p12).

📌 Explicación de cada parámetro:

-genkeypair → Genera un par de claves (pública y privada).
-alias servidor → Nombre con el que se identificará la clave en el almacén.
-keyalg RSA → Algoritmo de clave RSA.
-keysize 2048 → Tamaño de la clave en bits (2048 bits, recomendado para seguridad).
-validity 365 → Validez del certificado en días (1 año).
-keystore servidor_keystore.p12 → Archivo donde se guarda el almacén de claves.
-storetype PKCS12 → Formato del almacén de claves (.p12 en lugar de JKS).
-storepass 1234567 → Contraseña para proteger el almacén.
📌 Resultado:

Se crea el archivo servidor_keystore.p12 que contiene:
Clave privada
Clave pública
Certificado auto-firmado asociado
2️⃣ Comando: Exportar solo la clave pública del certificado
sh
Copiar
Editar
keytool -export -alias servidor -keystore servidor_keystore.p12 -file servidor_publico.cer -storepass 1234567
✔ Este comando extrae solo el certificado público (clave pública) del almacén servidor_keystore.p12 y lo guarda en un archivo .cer (X.509).

📌 Explicación de cada parámetro:

-export → Exporta un certificado.
-alias servidor → Nombre de la clave dentro del almacén de donde se extraerá el certificado.
-keystore servidor_keystore.p12 → Archivo que contiene la clave pública y privada.
-file servidor_publico.cer → Archivo de salida con el certificado público.
-storepass 1234567 → Contraseña del almacén para acceder a la clave.
📌 Resultado:

Se genera un archivo servidor_publico.cer que contiene:
Solo la clave pública del certificado
NO incluye la clave privada
