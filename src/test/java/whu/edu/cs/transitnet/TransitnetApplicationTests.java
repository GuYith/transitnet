package whu.edu.cs.transitnet;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import whu.edu.cs.transitnet.service.RoutesService;
import whu.edu.cs.transitnet.service.ShapesService;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("whu.edu.cs.transitnet.*")
class TransitnetApplicationTests {
    @Resource
    public RoutesService routesService;
    @Resource
    public ShapesService shapesService;
    @Test
    void contextLoads() {
    }

    @Test
    void CRUDTest() {

    }

}
