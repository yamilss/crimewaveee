# Guía de Solución: Servicio no responde en 3.21.53.102:8080

## Problema Identificado
Error: `ECONNREFUSED 3.21.53.102:8080` indica que el servicio Spring Boot NO está ejecutándose en el servidor EC2.

## Pasos para Solucionarlo

### 1. Verificar si tienes el archivo .pem para conectarte por SSH
Necesitas el archivo de llave privada (.pem) que usaste al crear la instancia EC2.

### 2. Conectarse al servidor EC2 por SSH
```bash
ssh -i "ruta/a/tu-archivo.pem" ubuntu@3.21.53.102
```

### 3. Una vez conectado, verificar el estado:
```bash
# Verificar si el servicio está ejecutándose
ps aux | grep java

# Verificar si el archivo JAR existe
ls -la ~/microservice-0.0.1-SNAPSHOT.jar

# Verificar logs si existen
tail -f ~/logs/crimewave.log
```

### 4. Si el servicio NO está ejecutándose, iniciarlo:
```bash
# Crear directorio de logs
mkdir -p ~/logs

# Iniciar el servicio
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &
```

### 5. Verificar que el puerto esté abierto:
```bash
# Verificar que el puerto 8080 esté en uso
netstat -tlnp | grep :8080
```

### 6. Si no tienes los archivos en el servidor, desplegarlos:
```bash
# Desde tu máquina local, ejecutar:
./deploy-to-aws.sh 3.21.53.102 ruta/a/tu-archivo.pem
```

## Una vez que el servicio esté funcionando

### URLs para Postman:
- **URL Base**: `http://3.21.53.102:8080/api/products`

### Endpoints para probar:
1. **Inicializar datos** (hacer PRIMERO):
   - Método: `POST`
   - URL: `http://3.21.53.102:8080/api/products/init-sample-data`

2. **Obtener todos los productos**:
   - Método: `GET` 
   - URL: `http://3.21.53.102:8080/api/products`

3. **Obtener productos por categoría**:
   - Método: `GET`
   - URL: `http://3.21.53.102:8080/api/products/category/POLERAS`

4. **Obtener productos destacados**:
   - Método: `GET`
   - URL: `http://3.21.53.102:8080/api/products/featured`

## Comandos de Diagnóstico Rápido

### Desde tu PC (para probar conectividad):
```bash
# Probar si el puerto está abierto
telnet 3.21.53.102 8080

# O usar curl
curl http://3.21.53.102:8080/api/products
```

### Desde el servidor EC2:
```bash
# Ver todos los procesos Java
pgrep -f java

# Matar procesos Java si es necesario
pkill -f microservice

# Ver si hay algo usando el puerto 8080
lsof -i :8080
```

## Notas Importantes

1. **Security Groups**: Tienes bien configurado el puerto 8080, eso está correcto.
2. **El problema principal**: El servicio Spring Boot no está ejecutándose.
3. **Solución más rápida**: Conectarse por SSH y reiniciar manualmente el servicio.

## ¿No tienes la clave SSH?
Si no tienes la clave .pem, necesitarás:
1. Ir a la consola AWS EC2
2. Seleccionar tu instancia
3. Usar "EC2 Instance Connect" para conectarte desde el navegador
4. Ejecutar los comandos de verificación e inicio del servicio
