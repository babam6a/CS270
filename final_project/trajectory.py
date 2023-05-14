import math

def cal_z_angle(d,x) :
    return int(math.degrees(math.atan(x/d)))

# cos(z_angle) = d/x -> d / cos(z_angle) = x
def cal_trajectory(d, z_angle, h_init, g, y, max_v) :
    x = d / math.cos(math.radians(z_angle))
    for a in range(1, 90) :
        rad_a = math.radians(a)
        try :
            v = math.sqrt((g * x * x) / (((h_init-y) + x * math.tan(rad_a)) * 2 * math.cos(rad_a) * math.cos(rad_a)))
        except :
            continue
        if v < max_v :
            print("a: %.3f, v: %.3f"%(a, v))
            return (a, v)
    return (0, 0)

