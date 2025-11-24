# 🚨 SOLUCIÓN RÁPIDA: ERRORES RDS FREE TIER

## ❌ **ERROR**: "backup retention period exceeds maximum"

### 🔧 **SOLUCIÓN INMEDIATA:**
Cuando configures RDS, en la sección **"Additional Configuration"**:

1. **Backup retention period**: Cambiar de `7 days` a `1 day`
2. **Enhanced monitoring**: `Disable` (NO activar)
3. **Performance Insights**: `Disable` (NO activar)

---

## ❌ **ERROR**: "Multi-AZ deployment not supported"

### 🔧 **SOLUCIÓN:**
- **Availability Zone**: Seleccionar `Single-AZ` 
- **NO seleccionar** Multi-AZ deployment

---

## ❌ **ERROR**: "Storage size exceeds free tier limit"

### 🔧 **SOLUCIÓN:**
- **Allocated storage**: Máximo `20 GB`
- **Storage autoscaling**: `Disable`

---

## ❌ **ERROR**: "Instance class not eligible for free tier"

### 🔧 **SOLUCIÓN:**
- **DB instance class**: Solo `db.t3.micro` o `db.t2.micro`
- Verificar que diga **"Free tier eligible"**

---

## ✅ **CONFIGURACIÓN CORRECTA PARA FREE TIER:**

```
Engine: PostgreSQL
Templates: Free tier ✓
DB instance identifier: crimewave-db
Master username: postgres
Master password: CrimeWave2024!

Instance configuration:
- DB instance class: db.t3.micro ✓

Storage:
- Storage type: General Purpose SSD (gp2) ✓
- Allocated storage: 20 GB ✓
- Storage autoscaling: Disable ✓

Connectivity:
- Public access: Yes ✓
- VPC security group: Default ✓

Additional configuration:
- Database name: crimewave_products ✓
- Backup retention period: 1 day ✓ (CLAVE)
- Enhanced monitoring: Disable ✓ (CLAVE)
- Performance Insights: Disable ✓ (CLAVE)
- Multi-AZ deployment: No ✓ (CLAVE)
```

---

## 🎯 **SI YA TIENES ERROR, REINTENTAR:**

1. **Cancelar** la creación actual
2. **Volver a "Create Database"**
3. **Asegurate** de seleccionar `Templates: Free tier`
4. **Expandir** "Additional configuration"
5. **Cambiar** backup retention a `1 day`
6. **Desactivar** Enhanced monitoring
7. **Desactivar** Performance Insights
8. **Create database**

---

## 💡 **VERIFICACIÓN ANTES DE CREAR:**

- [ ] Template: "Free tier" seleccionado
- [ ] Instance class: db.t3.micro o db.t2.micro
- [ ] Storage: 20 GB máximo
- [ ] Backup retention: 1 día
- [ ] Enhanced monitoring: Desactivado
- [ ] Performance Insights: Desactivado
- [ ] Multi-AZ: No

**¡Con esta configuración debería crear sin problemas!** 🎉
