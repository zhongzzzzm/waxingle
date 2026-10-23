#!/usr/bin/env python3
"""生成 static/tabbar 下的 81x81 tab 图标（PNG，RGBA，透明底）。

纯 Python 标准库实现，无需安装依赖。修改图形/配色后重跑：
    python3 scripts/gen_tabbar_icons.py
CI 用 --check 校验仓库图标与脚本输出一致（比对像素，忽略压缩器差异）：
    python3 scripts/gen_tabbar_icons.py --check
"""
import math
import os
import struct
import sys
import zlib

SIZE = 81
SS = 4  # 超采样倍数（抗锯齿）

GRAY = (143, 138, 124)   # #8f8a7c 未选中
AMBER = (255, 200, 74)   # #ffc84a 选中


def in_circle(x, y, cx, cy, r):
    return (x - cx) ** 2 + (y - cy) ** 2 <= r * r


def moon(x, y):
    return in_circle(x, y, 0.48, 0.52, 0.30) and not in_circle(x, y, 0.64, 0.36, 0.28)


def clock(x, y):
    d = math.hypot(x - 0.5, y - 0.5)
    if abs(d - 0.32) <= 0.045:
        return True
    if 0.475 <= x <= 0.525 and 0.30 <= y <= 0.52:      # 时针（向上）
        return True
    if 0.50 <= x <= 0.70 and 0.475 <= y <= 0.525:      # 分针（向右）
        return True
    return False


def build_raw(color, shape):
    """渲染为未压缩的 PNG 像素字节（含每行 filter 字节）。"""
    raw = b''
    for py in range(SIZE):
        raw += b'\x00'
        for px in range(SIZE):
            cov = 0
            for sy in range(SS):
                for sx in range(SS):
                    x = (px + (sx + 0.5) / SS) / SIZE
                    y = (py + (sy + 0.5) / SS) / SIZE
                    if shape(x, y):
                        cov += 1
            alpha = int(round(255 * cov / (SS * SS)))
            raw += bytes((color[0], color[1], color[2], alpha))
    return raw


def chunk(tag, data):
    return (struct.pack('>I', len(data)) + tag + data
            + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff))


def png_from_raw(raw):
    ihdr = struct.pack('>IIBBBBB', SIZE, SIZE, 8, 6, 0, 0, 0)
    return (b'\x89PNG\r\n\x1a\n'
            + chunk(b'IHDR', ihdr)
            + chunk(b'IDAT', zlib.compress(raw))
            + chunk(b'IEND', b''))


def read_png_raw(data):
    """解出 PNG 的像素字节，供 --check 比对（不受 zlib 压缩器差异影响）。"""
    pos = 8
    idat = b''
    width = height = depth = color_type = None
    while pos < len(data):
        (length,) = struct.unpack('>I', data[pos:pos + 4])
        tag = data[pos + 4:pos + 8]
        body = data[pos + 8:pos + 8 + length]
        if tag == b'IHDR':
            width, height, depth, color_type = struct.unpack('>IIBB', body[:10])
        elif tag == b'IDAT':
            idat += body
        pos += 12 + length
    if (width, height, depth, color_type) != (SIZE, SIZE, 8, 6):
        raise ValueError('unexpected IHDR')
    return zlib.decompress(idat)


def main(argv):
    out = os.path.normpath(os.path.join(os.path.dirname(__file__), '..', 'static', 'tabbar'))
    check = '--check' in argv
    files = [('record', '', moon), ('record', '-active', moon),
             ('history', '', clock), ('history', '-active', clock)]
    if not check:
        os.makedirs(out, exist_ok=True)
    stale = False
    for name, suffix, shape in files:
        color = AMBER if suffix else GRAY
        path = os.path.join(out, name + suffix + '.png')
        raw = build_raw(color, shape)
        if check:
            with open(path, 'rb') as f:
                if read_png_raw(f.read()) != raw:
                    print('OUTDATED:', path)
                    stale = True
        else:
            with open(path, 'wb') as f:
                f.write(png_from_raw(raw))
            print('generated:', path)
    if check:
        if stale:
            print('tab 图标与脚本不一致，请运行 python3 scripts/gen_tabbar_icons.py 后提交')
            return 1
        print('tab 图标与脚本一致')
    return 0


if __name__ == '__main__':
    raise SystemExit(main(sys.argv))
