## ⭐1.วิธีดึงโค้ดจาก branch `master` มาไว้ที่ branch ของตัวเอง 

> ใช้ในกรณีที่ต้องการอัปเดตงานล่าสุดจาก `master` มาใน branch ของตนเอง
> เช่น ให้ branch `grace-backend` มีโค้ดตรงกับ `master`

---

### 🔹 ขั้นตอนที่ 1 : เปิด Terminal และเข้าโฟลเดอร์โปรเจกต์

```bash
cd helloDevops-backend
หรือ
cd backend 
```

---

### 🔹 ขั้นตอนที่ 2 : ตรวจสอบว่าอยู่ branch ของตัวเอง

```bash
git branch
```

ถ้ายังไม่อยู่ที่ branch ตัวเอง ให้ใช้คำสั่งนี้เพื่อสลับไป

```bash
git checkout grace-backend
```

> *(แทนชื่อ branch ของตัวเอง เช่น `pair-backend`, `ploy-backend` ได้ตามต้องการ)*

---

### 🔹 ขั้นตอนที่ 3 : อัปเดตข้อมูล branch ทั้งหมดจาก GitHub

```bash
git fetch --all --prune
```

---

### 🔹 ขั้นตอนที่ 4 : รวมโค้ดจาก `master` เข้ามาใน branch ตัวเอง

#### ✅ วิธีที่ 1 (แนะนำ) — รวมแบบ merge ปลอดภัย เห็นประวัติการรวม

```bash
git merge origin/master -m "merge: master → grace-backend"
```

#### ⚠️ วิธีที่ 2 (ถ้าอยากให้โค้ด master มาทับทั้งหมด)

> ใช้กรณีที่ต้องการให้ branch ของตัวเองเหมือน master ทุกไฟล์
> และไม่สนโค้ดเก่าของตัวเอง

```bash
git reset --hard origin/master
```

---

### 🔹 ขั้นตอนที่ 5 : อัปโหลดขึ้น GitHub อีกครั้ง

```bash
git push origin grace-backend --force
```

---

### 🔹 สรุปคำสั่งทั้งหมด (คัดลอกใส่เทอร์มินัลได้เลย)

```bash
cd backend
git checkout grace-backend
git fetch --all --prune
git merge origin/master -m "merge: master → grace-backend"
git push origin grace-backend
```

> ถ้าอยากให้โค้ดจาก master ทับทุกไฟล์ → แทนบรรทัด merge ด้วย
> `git reset --hard origin/master`

---

### 💡 หมายเหตุ

* ทุกครั้งก่อน merge หรือ reset ควร `git pull` ก่อน เพื่ออัปเดตโค้ดตัวเองให้ใหม่สุด
* ถ้ามี conflict ให้แก้ไขไฟล์ที่ชนกัน แล้ว commit ก่อน push
* ใช้ `git log --oneline --graph` เพื่อตรวจสอบประวัติ commit หลัง merge

---

## ⭐2.วิธีรัน Backend + MySQL บน Docker ใหม่จากศูนย์ 🐳 

> ใช้เมื่อมีการแก้ไขฐานข้อมูล, ปรับ Dockerfile, หรืออยากเริ่มรันระบบใหม่ทั้งหมดแบบ clean  
> (เช่น หลังอัปเดต schema, แก้ไขโค้ด backend แล้ว build ใหม่)

---

## ⚙️ ขั้นตอนการใช้งาน

### 🔹 1️⃣ ปิดและลบ container + volume ทั้งหมดที่เคยรันไว้
```bash
docker compose down -v
````

> คำสั่งนี้จะลบ container ทั้งหมดใน `docker-compose.yml`
> พร้อมกับ volume ที่เก็บข้อมูล MySQL (รีเซ็ตฐานข้อมูลใหม่ทั้งหมด)

---

### 🔹 2️⃣ รันเฉพาะ MySQL ขึ้นมาก่อน

```bash
docker compose up -d mysql
```

> เพื่อให้ MySQL พร้อมก่อน build backend
> ระบบจะสร้าง container ชื่อ `mysql` และรอจนขึ้นสถานะ `(healthy)`

---

### 🔹 3️⃣ ตรวจสอบสถานะของ MySQL

```bash
docker ps -a --filter "name=mysql"
```

ตัวอย่างผลลัพธ์ที่ถูกต้อง ✅

```
CONTAINER ID   IMAGE       STATUS                   PORTS
6cc71a0f5d30   mysql:8.0   Up 5 minutes (healthy)   0.0.0.0:3307->3306/tcp
```

> ✅ ต้องมีคำว่า `(healthy)` แสดงว่า MySQL พร้อมเชื่อมต่อแล้ว

---

### 🔹 4️⃣ ตรวจสอบ log ของ MySQL

```bash
docker logs -n 200 mysql
```

> เพื่อดูว่ามี error หรือไม่ (เช่น password ผิด, database สร้างไม่สำเร็จ ฯลฯ)

---

### 🔹 5️⃣ ทดสอบเข้าใช้งานฐานข้อมูล MySQL ภายใน container

```bash
docker exec -it mysql mysql -h 127.0.0.1 -uroot -p1234
```

> เปลี่ยน `-p1234` เป็นรหัสผ่านที่ตั้งไว้ใน `docker-compose.yml`

ภายใน MySQL prompt:

```sql
SHOW DATABASES;
USE hellodevops;
SHOW TABLES;
EXIT;
```

ตัวอย่างผลลัพธ์ ✅

```
+--------------------+
| Database           |
+--------------------+
| hellodevops        |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```

---

### 🔹 6️⃣ สร้าง backend container ใหม่แบบไม่ใช้ cache

```bash
docker compose build --no-cache backend
```

> เพื่อให้ Docker สร้าง image ของ backend ใหม่ทั้งหมด
> ป้องกันการใช้ layer เก่าที่อาจค้างจากการ build ครั้งก่อน

---

### 🔹 7️⃣ รัน backend และตรวจสอบ log

```bash
docker compose up -d backend
docker logs -f backend
```

> เมื่อรันสำเร็จ backend จะขึ้นข้อความประมาณนี้:

```
:: Spring Boot :: (v3.5.x)
Started BackendApplication in 10.5 seconds
```

---

## ✅ สรุปการทำงาน

| ขั้นตอน            | คำสั่ง                                      | ผลลัพธ์           |
| ------------------ | ------------------------------------------- | ----------------- |
| ลบ container เก่า  | `docker compose down -v`                    | ล้างระบบทั้งหมด   |
| รัน MySQL          | `docker compose up -d mysql`                | MySQL พร้อมใช้งาน |
| ตรวจสอบสถานะ       | `docker ps -a --filter "name=mysql"`        | ขึ้น `(healthy)`  |
| ทดสอบเข้า DB       | `docker exec -it mysql mysql -uroot -p1234` | เข้าดูตารางได้    |
| Build backend ใหม่ | `docker compose build --no-cache backend`   | สร้าง image ใหม่  |
| รัน backend        | `docker compose up -d backend`              | ระบบพร้อมใช้งาน   |
| Build frontend ใหม่ | `docker compose build --no-cache frontend`   | สร้าง image ใหม่  |
| รัน frontend        | `docker compose up -d frontend`              | ระบบพร้อมใช้งาน   |

---

💡 **Tips:**

* ถ้า backend ขึ้น `HTTP 500` ให้ตรวจ log backend ด้วย

  ```bash
  docker logs -f backend
  ```
* ถ้าต้องการให้ backend เริ่มทำงานหลัง MySQL พร้อมทุกครั้ง
  ให้เพิ่มใน `docker-compose.yml`:

  ```yaml
  depends_on:
    mysql:
      condition: service_healthy
  ```

---

🎉 **ผลลัพธ์สุดท้าย:**
เมื่อทำครบทุกขั้นตอน

* MySQL จะพร้อมใช้งาน (port 3307)
* backend จะเชื่อมต่อฐานข้อมูล `hellodevops` ได้
* สามารถเรียก API เช่น `http://localhost:8080/api/products` ได้ทันที 🚀
---
## ⭐3. วิธีเอา branch ploy มารวมเข้ากับ branch pair
```
# Pull branch
cd helloDevops-backend
git fetch --all --prune
git checkout pair-backend
git merge origin/ploy-backend -m "merge backend: update from ploy → pair"
git push origin pair-backend
```

ให้เพื่อน ๆ **สามารถเรียกใช้ (checkout)** branch เหล่านี้ได้
ในเครื่องของพวกเขา (ที่เคย clone repo `helloDevops-backend` ไว้แล้ว)

---

## ⭐4.ให้เพื่อน ๆ ดึง branch ที่คุณสร้าง (`fluke-backend`, `mint-backend`)
> มาใช้งานในเครื่องของตัวเองได้

---

## ⚙️ ขั้นตอนสำหรับเพื่อนแต่ละคน (ทีละบรรทัด)

### 🔹 1️⃣ เข้าโฟลเดอร์โปรเจกต์ backend

(ของเพื่อนในเครื่องตัวเอง)

```bash
cd C:\Users\<ชื่อเพื่อน>\Desktop\HelloDevops\helloDevops-backend
```

---

### 🔹 2️⃣ ดึง branch ล่าสุดจาก GitHub

```bash
git fetch --all --prune
```

> 🔸 คำสั่งนี้จะโหลด branch ใหม่ทั้งหมดที่ถูกสร้างใน GitHub
> 🔸 รวมถึง branch ที่คุณสร้าง (`fluke-backend`, `mint-backend`)

---

### 🔹 3️⃣ ดูรายการ branch ทั้งหมด

```bash
git branch -a
```

เพื่อนจะเห็นรายการประมาณนี้ 👇

```
* master
  remotes/origin/master
  remotes/origin/fluke-backend
  remotes/origin/mint-backend
```

---

### 🔹 4️⃣ สลับมาใช้ branch ของตัวเอง

**ถ้าเป็น Fluke:**

```bash
git checkout -b fluke-backend origin/fluke-backend
```

**ถ้าเป็น Mint:**

```bash
git checkout -b mint-backend origin/mint-backend
```

> ✅ คำสั่งนี้จะสร้าง branch ในเครื่องของเพื่อน
> และเชื่อมต่อกับ branch บน GitHub (`origin/<branch>`)

---

### 🔹 5️⃣ ตรวจสอบว่าอยู่ branch ตัวเองแล้ว

```bash
git branch
```

จะเห็นว่า:

```
* fluke-backend
  master
  mint-backend
```

หรือถ้าเป็น Mint ก็จะเห็น:

```
* mint-backend
  master
  fluke-backend
```

---

### 🔹 6️⃣ จากนั้นทำงานตามปกติ

* แก้ไขโค้ด
* commit
* push ขึ้น branch ของตัวเอง

ตัวอย่าง (Fluke):

```bash
git add -A
git commit -m "feat: add new API for login validation"
git push origin fluke-backend
```

---

## สรุปสำหรับเพื่อนแต่ละคน

| ชื่อเพื่อน | คำสั่งที่ใช้                                                             |
| ---------- | ------------------------------------------------------------------------ |
| Fluke      | `git fetch --all` → `git checkout -b fluke-backend origin/fluke-backend` |
| Mint       | `git fetch --all` → `git checkout -b mint-backend origin/mint-backend`   |

---

## 💡 Tips เพิ่มเติม

* ถ้าเพื่อน clone โปรเจกต์ใหม่เลย (ยังไม่เคยมีในเครื่อง)
  ก็จะเห็น branch เหล่านี้อยู่แล้วใน GitHub → สามารถ checkout ได้เลยเช่นกัน

  ```bash
  git clone https://github.com/helloDevops2025/helloDevops-backend.git
  cd helloDevops-backend
  git fetch --all
  git checkout fluke-backend   # หรือ mint-backend
  ```

---

🎉 **หลังจากนี้:**

* Fluke ทำงานบน `fluke-backend`
* Mint ทำงานบน `mint-backend`
* คุณ (หรือทีม lead) สามารถรวมกลับไปที่ `master` เมื่อพร้อม

---

## ⭐5.ขั้นตอนการ Push โค้ด Backend แบบมีการแก้ไขไฟล์

### 1. ตรวจสอบว่าอยู่ใน branch ที่ต้องการ
```bash
git branch
```
- ต้องอยู่ใน branch เช่น `ploy-backend`  
- ถ้ายังไม่อยู่ → ใช้ `git checkout ploy-backend`

---

### 2. ตรวจสอบสถานะไฟล์ที่แก้
```bash
git status
```
- ดูว่าไฟล์ไหนถูกแก้ และยังไม่ได้ `add` หรือ `commit`

---

### 3. เพิ่มไฟล์ที่แก้เข้าสู่ staging
```bash
# 3 ตัวเลือก
git add .  
git add -A 
git add <ตามด้วยชื่อไฟล์>
```
- หรือถ้าอยากเลือกเฉพาะไฟล์ → `git add path/to/file`

---

### 4. Commit พร้อมข้อความสรุปงาน
```bash
git commit -m "Finish Phase X: [สรุปสิ่งที่แก้ไข]"
```
- เช่น: `Finish Phase 1: backend-fix ProductRepository.java`

---

### 5. ดึงการเปลี่ยนแปลงจาก remote ก่อน push
```bash
git pull
```
- ป้องกัน error `rejected (fetch first)`  
- ถ้าอยากให้ merge แบบเรียบง่าย → ใช้ `git pull --rebase`

---

### 6. Push ขึ้น remote
```bash
git push origin ploy-backend
```
- ถ้าไม่มี error แสดงว่า push สำเร็จ

---
## สรุป ขั้นตอนPush โค้ด Backend แบบมีการแก้ไขไฟล์
```bash
git branch
git status

# 3 ตัวเลือก
git add .  
git add -A 
git add <ตามด้วยชื่อไฟล์>

git status
git commit -m "Finish Phase X: [สรุปสิ่งที่แก้ไข]"
git pull 
git push origin ploy-backend
```
---

## ⚠️ กรณีเจอ error: `rejected (fetch first)`

- ให้ใช้ `git pull` ก่อน แล้วค่อย `git push` ใหม่
- ถ้าไม่อยาก merge manual → ใช้ `git pull --rebase`

---

## กรณีต้องการล้าง branch ให้เหมือน develop (เช่นเตรียม branch ใหม่)

```bash
git checkout ploy-backend
git fetch --all --prune
git reset --hard origin/develop
git push origin ploy-backend --force
```
> ใช้เมื่ออยากให้ branch `ploy-backend` เหมือน `develop` เป๊ะ  
> ⚠️ `--force` จะเขียนทับประวัติเดิม ต้องแจ้งทีมก่อนใช้

---
