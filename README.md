## 🧭 วิธีดึงโค้ดจาก branch `master` มาไว้ที่ branch ของตัวเอง

> ใช้ในกรณีที่ต้องการอัปเดตงานล่าสุดจาก `master` มาใน branch ของตนเอง
> เช่น ให้ branch `grace-backend` มีโค้ดตรงกับ `master`

---

### 🔹 ขั้นตอนที่ 1 : เปิด Terminal และเข้าโฟลเดอร์โปรเจกต์

```bash
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
